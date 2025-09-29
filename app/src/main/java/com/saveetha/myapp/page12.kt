package com.saveetha.myapp

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.saveetha.myapp.network.ApiClient
import com.saveetha.myapp.ui.auth.AddTaskResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class page12 : AppCompatActivity() {

    private lateinit var taskName: EditText
    private lateinit var frequencySpinner: Spinner
    private lateinit var lastDone: EditText
    private lateinit var nextDue: EditText
    private lateinit var saveTaskButton: Button
    private lateinit var deleteTaskButton: Button
    private var catId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_page12)

        catId = intent.getIntExtra("catid", -1)
        Log.d("AddingDebug", "Cat ID Used in API: $catId")

        taskName = findViewById(R.id.taskName)
        frequencySpinner = findViewById(R.id.frequencySpinner)
        lastDone = findViewById(R.id.lastDone)
        nextDue = findViewById(R.id.nextDue)
        saveTaskButton = findViewById(R.id.saveTaskButton)
        deleteTaskButton = findViewById(R.id.deleteTaskButton)

        // Spinner setup
        val frequencies = arrayOf("Daily", "Weekly", "Monthly")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, frequencies)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        frequencySpinner.adapter = adapter

        // Date pickers
        lastDone.setOnClickListener { showDatePickerDialog(lastDone) }
        nextDue.setOnClickListener { showDatePickerDialog(nextDue) }

        // Save button
        saveTaskButton.setOnClickListener {
            addTask()
        }
    }

    private fun showDatePickerDialog(editText: EditText) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePicker = DatePickerDialog(this, { _, y, m, d ->
            val formattedDate = String.format("%04d/%02d/%02d", y, m + 1, d)
            editText.setText(formattedDate)
        }, year, month, day)

        datePicker.show()
    }

    private fun addTask() {
        val task = taskName.text.toString().trim()
        val frequency = frequencySpinner.selectedItem.toString()
        val last = lastDone.text.toString().trim()
        val next = nextDue.text.toString().trim()

        if (task.isEmpty() || last.isEmpty() || next.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        ApiClient.apiService.addTask(catId, task, frequency, last, next)
            .enqueue(object : Callback<AddTaskResponse> {
                override fun onResponse(call: Call<AddTaskResponse>, response: Response<AddTaskResponse>) {
                    if (response.isSuccessful) {
                        val apiResponse = response.body()
                        Toast.makeText(this@page12, apiResponse?.message, Toast.LENGTH_SHORT).show()

                        // ✅ Return result to GroomingScheduleActivity
                        val resultIntent = Intent()
                        resultIntent.putExtra("updated", true)
                        setResult(RESULT_OK, resultIntent)
                        finish()
                    } else {
                        Toast.makeText(this@page12, "Server error", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<AddTaskResponse>, t: Throwable) {
                    Toast.makeText(this@page12, "Failed: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
    }
}
