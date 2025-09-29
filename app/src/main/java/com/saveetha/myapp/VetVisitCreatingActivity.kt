package com.saveetha.myapp.ui

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.saveetha.myapp.R
import com.saveetha.myapp.network.ApiClient
import com.saveetha.myapp.network.VetAppointmentResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class VetVisitCreatingActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var selectDate: Button
    private lateinit var selectTime: Button
    private lateinit var spinnerVisitType: Spinner
    private lateinit var clinicName: EditText
    private lateinit var vetName: EditText
    private lateinit var vetPhone: EditText
    private lateinit var lastVisit: Button
    private lateinit var saveButton: Button
    private lateinit var clearButton: Button
    private lateinit var progressBar: ProgressBar

    private lateinit var imagePet: ImageView
    private lateinit var textPetName: TextView
    private lateinit var textPetDetails: TextView

    private var catId: Int = -1
    private var catName: String = ""
    private var catBreed: String = ""
    private var catAge: String = ""
    private var catPhoto: String = ""

    private val visitTypes = arrayOf("Check-up", "Vaccination", "Surgery", "Dental", "Emergency", "Other")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.vetvisitcreating)

        setupToolbar()
        initViews()
        setupListeners()

        // Retrieve extras from intent
        catId = intent.getIntExtra("catid", -1)
        catName = intent.getStringExtra("catname").orEmpty()
        catBreed = intent.getStringExtra("catbreed").orEmpty()
        catAge = intent.getStringExtra("catage").orEmpty()
        catPhoto = intent.getStringExtra("catphoto").orEmpty()

        Log.d("VetVisitCreating", "Received Cat Info: name=$catName breed=$catBreed age=$catAge photo=$catPhoto")

        // Set pet name UI
        if (catName.isBlank()) {
            textPetName.visibility = View.GONE
        } else {
            textPetName.text = catName
            textPetName.visibility = View.VISIBLE
        }

        // Set pet details UI (breed & age)
        val petDetails = listOf(catBreed, catAge).filter { it.isNotBlank() }.joinToString(" • ")
        if (petDetails.isBlank()) {
            textPetDetails.visibility = View.GONE
        } else {
            textPetDetails.text = petDetails
            textPetDetails.visibility = View.VISIBLE
        }

        // Load pet photo
        if (catPhoto.isNotBlank()) {
            Glide.with(this)
                .load(catPhoto)
                .placeholder(R.drawable.luna)
                .error(R.drawable.luna)
                .circleCrop()
                .into(imagePet)
        } else {
            imagePet.setImageResource(R.drawable.luna)
        }

        progressBar.isVisible = false
    }

    private fun setupToolbar() {
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(false)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            finish()
            true
        } else super.onOptionsItemSelected(item)
    }

    private fun initViews() {
        imagePet = findViewById(R.id.image_pet)
        textPetName = findViewById(R.id.text_pet_name)
        textPetDetails = findViewById(R.id.text_pet_details)
        selectDate = findViewById(R.id.button_select_date)
        selectTime = findViewById(R.id.button_select_time)
        spinnerVisitType = findViewById(R.id.spinner_visit_type)
        clinicName = findViewById(R.id.edit_clinic_name)
        vetName = findViewById(R.id.edit_vet_name)
        vetPhone = findViewById(R.id.edit_phone)
        lastVisit = findViewById(R.id.button_last_visit)
        saveButton = findViewById(R.id.button_save)
        clearButton = findViewById(R.id.button_delete)
        progressBar = findViewById(R.id.progressBar)

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, visitTypes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerVisitType.adapter = adapter
    }

    private fun setupListeners() {
        selectDate.setOnClickListener { showDatePicker { selectDate.text = it } }
        selectTime.setOnClickListener { showTimePicker { selectTime.text = it } }
        lastVisit.setOnClickListener { showDatePicker { lastVisit.text = it } }
        saveButton.setOnClickListener { saveAppointment() }
        clearButton.setOnClickListener { clearFields() }
    }

    private fun showDatePicker(callback: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(this, { _, year, month, day ->
            callback(String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, day))
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun showTimePicker(callback: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        TimePickerDialog(this, { _, hour, minute ->
            callback(String.format(Locale.getDefault(), "%02d:%02d", hour, minute))
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
    }

    private fun saveAppointment() {
        val date = selectDate.text.toString()
        val time = selectTime.text.toString()
        val visitType = spinnerVisitType.selectedItem.toString()
        val clinic = clinicName.text.toString().trim()
        val vet = vetName.text.toString().trim()
        val phone = vetPhone.text.toString().trim()
        val lastVisit = lastVisit.text.toString()

        if (catId == -1 || date.startsWith("Select") || time.startsWith("Select") || clinic.isEmpty() || vet.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show()
            return
        }

        progressBar.isVisible = true
        saveButton.isEnabled = false

        ApiClient.apiService.addVetAppointment(
            catid = catId,
            date = date,
            time = time,
            visitType = visitType,
            clinicName = clinic,
            vetName = vet,
            vetPhone = phone,
            lastVisit = lastVisit
        ).enqueue(object : Callback<VetAppointmentResponse> {
            override fun onResponse(call: Call<VetAppointmentResponse>, response: Response<VetAppointmentResponse>) {
                progressBar.isVisible = false
                saveButton.isEnabled = true
                if (response.isSuccessful && response.body()?.status == "success") {
                    Toast.makeText(this@VetVisitCreatingActivity, "Appointment saved", Toast.LENGTH_SHORT).show()
                    setResult(Activity.RESULT_OK, intent.apply { putExtra("updated", true) })
                    finish()
                } else {
                    Toast.makeText(this@VetVisitCreatingActivity, "Failed to save appointment", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<VetAppointmentResponse>, t: Throwable) {
                progressBar.isVisible = false
                saveButton.isEnabled = true
                Toast.makeText(this@VetVisitCreatingActivity, "Network error: ${t.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun clearFields() {
        selectDate.text = getString(R.string.select_date)
        selectTime.text = getString(R.string.select_time)
        spinnerVisitType.setSelection(0)
        clinicName.text.clear()
        vetName.text.clear()
        vetPhone.text.clear()
        lastVisit.text = getString(R.string.select_date)
        Toast.makeText(this, "Form cleared", Toast.LENGTH_SHORT).show()
    }
}
