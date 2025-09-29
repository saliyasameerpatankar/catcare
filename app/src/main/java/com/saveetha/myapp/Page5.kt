package com.saveetha.myapp

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.saveetha.myapp.models.CatProfileUploadResponse
import com.saveetha.myapp.network.ApiClient
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Page5 : AppCompatActivity() {

    private lateinit var editCatName: EditText
    private lateinit var editBreed: EditText
    private lateinit var editAge: EditText
    private lateinit var genderRadioGroup: RadioGroup
    private lateinit var btnSave: Button
    private lateinit var btnCancel: TextView

    private var currentUserId: Int = -1
    // If you plan to support photo later:
    // private var currentPhotoFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_page5)

        currentUserId = intent.getIntExtra("userId", -1)
        if (currentUserId == -1) {
            Toast.makeText(this, "Invalid user session", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        editCatName = findViewById(R.id.edit_cat_name)
        editBreed = findViewById(R.id.edit_breed)
        editAge = findViewById(R.id.edit_age)
        genderRadioGroup = findViewById(R.id.genderRadioGroup)
        btnSave = findViewById(R.id.btn_save)
        btnCancel = findViewById(R.id.btn_cancel)

        btnSave.setOnClickListener { saveCatProfile() }
        btnCancel.setOnClickListener { finish() }
    }

    private fun saveCatProfile() {
        val catName = editCatName.text.toString().trim()
        val breed = editBreed.text.toString().trim()
        val age = editAge.text.toString().trim()

        if (catName.isEmpty() || breed.isEmpty() || age.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val selectedGender = when (genderRadioGroup.checkedRadioButtonId) {
            R.id.radio_male -> "male"
            R.id.radio_female -> "female"
            else -> {
                Toast.makeText(this, "Please select gender", Toast.LENGTH_SHORT).show()
                return
            }
        }

        // Prepare multipart parts (photo is optional)
        val useridPart = currentUserId.toString().toRequestBody("text/plain".toMediaType())
        val catnamePart = catName.toRequestBody("text/plain".toMediaType())
        val breedPart = breed.toRequestBody("text/plain".toMediaType())
        val agePart = age.toRequestBody("text/plain".toMediaType())
        val genderPart = selectedGender.toRequestBody("text/plain".toMediaType())

        val photoPart: MultipartBody.Part? = null // No photo for now

        ApiClient.apiService.addCatProfileMultipart(
            useridPart, catnamePart, agePart, breedPart, genderPart, photoPart
        ).enqueue(object : Callback<CatProfileUploadResponse> {
            override fun onResponse(
                call: Call<CatProfileUploadResponse>,
                response: Response<CatProfileUploadResponse>
            ) {
                if (response.isSuccessful && response.body()?.status == "success") {
                    Toast.makeText(this@Page5, "Profile added successfully!", Toast.LENGTH_SHORT).show()
                    setResult(RESULT_OK)
                    finish()
                } else {
                    Toast.makeText(
                        this@Page5,
                        "Error: ${response.body()?.message ?: response.message()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }

            override fun onFailure(call: Call<CatProfileUploadResponse>, t: Throwable) {
                Toast.makeText(this@Page5, "Network error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}
