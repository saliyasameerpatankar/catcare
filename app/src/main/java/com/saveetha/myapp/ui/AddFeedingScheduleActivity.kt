package com.saveetha.myapp.ui

import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.saveetha.myapp.R
import java.util.*

class AddFeedingScheduleActivity : AppCompatActivity() {

    private lateinit var backBtn: ImageView
    private lateinit var feedingTimeEditText: EditText
    private lateinit var foodTypeSpinner: Spinner
    private lateinit var portionSizeEditText: EditText
    private lateinit var specialInstructionsEditText: EditText
    private lateinit var reminderSwitch: Switch
    private lateinit var saveBtn: Button
    private lateinit var deleteBtn: Button

    private val foodTypes = arrayOf("Dry Food", "Wet Food", "Mixed", "Raw", "Other")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.addfeedingschedule) // Link to your XML file

        // Initialize views
        backBtn = findViewById(R.id.backBtn)
        feedingTimeEditText = findViewById(R.id.feedingTime)
        foodTypeSpinner = findViewById(R.id.foodTypeSpinner)
        portionSizeEditText = findViewById(R.id.portionSize)
        specialInstructionsEditText = findViewById(R.id.specialInstructions)
        reminderSwitch = findViewById(R.id.feedingReminderSwitch)
        saveBtn = findViewById(R.id.saveScheduleBtn)
        deleteBtn = findViewById(R.id.deleteScheduleBtn)

        // Back Button
        backBtn.setOnClickListener {
            onBackPressed()
        }

        // Time Picker
        feedingTimeEditText.setOnClickListener {
            showTimePicker()
        }

        // Spinner setup
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, foodTypes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        foodTypeSpinner.adapter = adapter

        // Save button
        saveBtn.setOnClickListener {
            saveSchedule()
        }

        // Delete button
        deleteBtn.setOnClickListener {
            deleteSchedule()
        }

        // Switch (optional)
        reminderSwitch.setOnCheckedChangeListener { _, isChecked ->
            Toast.makeText(this, if (isChecked) "Reminders On" else "Reminders Off", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showTimePicker() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val picker = TimePickerDialog(this,
            { _, selectedHour, selectedMinute ->
                val amPm = if (selectedHour >= 12) "PM" else "AM"
                val hourFormatted = if (selectedHour % 12 == 0) 12 else selectedHour % 12
                val minuteFormatted = String.format("%02d", selectedMinute)
                val time = "$hourFormatted:$minuteFormatted $amPm"
                feedingTimeEditText.setText(time)
            },
            hour, minute, false)
        picker.show()
    }

    private fun saveSchedule() {
        val time = feedingTimeEditText.text.toString()
        val food = foodTypeSpinner.selectedItem.toString()
        val portion = portionSizeEditText.text.toString()
        val notes = specialInstructionsEditText.text.toString()
        val reminder = reminderSwitch.isChecked

        Toast.makeText(
            this,
            "Saved:\nTime: $time\nFood: $food\nPortion: $portion\nReminder: $reminder",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun deleteSchedule() {
        feedingTimeEditText.setText("")
        foodTypeSpinner.setSelection(0)
        portionSizeEditText.setText("")
        specialInstructionsEditText.setText("")
        reminderSwitch.isChecked = false

        Toast.makeText(this, "Schedule Deleted", Toast.LENGTH_SHORT).show()
    }
}
