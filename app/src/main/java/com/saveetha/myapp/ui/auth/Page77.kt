package com.saveetha.myapp.ui.auth

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.saveetha.myapp.R
import com.saveetha.myapp.network.ApiClient
import com.saveetha.myapp.ui.GroomingScheduleActivity
import com.saveetha.myapp.ui.PhotoBoothActivity
import com.saveetha.myapp.ui.health.WeightTrackingActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Page77 : AppCompatActivity() {

    private var catId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_page7)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Get intent extras
        catId = intent.getIntExtra("catid", -1)
        Log.d("Page7Debug", "Clicked cat -> id: $catId")

        val catName = intent.getStringExtra("catName") ?: "Unknown"
        val catBreed = intent.getStringExtra("catBreed") ?: "Unknown Breed"
        val catAge = intent.getStringExtra("catAge") ?: "Unknown Age"

        // UI elements
        val tvName = findViewById<TextView>(R.id.tvPetName)
        val tvDetails = findViewById<TextView>(R.id.tvPetDetails)
        val ivPhoto = findViewById<ImageView>(R.id.ivPetPhoto)
        val btnDelete = findViewById<Button>(R.id.btn_delete)

        // Set UI
        tvName.text = catName
        tvDetails.text = "$catBreed • $catAge"

        when (catName.lowercase()) {
            "luna" -> ivPhoto.setImageResource(R.drawable.luna)
            "milo" -> ivPhoto.setImageResource(R.drawable.milo)
            else -> ivPhoto.setImageResource(R.drawable.luna)
        }

        // Back button
        findViewById<ImageView>(R.id.ivBack).setOnClickListener {
            finish()
        }

        // Grooming button
        val groomingBtn = findViewById<LinearLayout>(R.id.groombtn)
        groomingBtn.setOnClickListener {
            val intent = Intent(this, GroomingScheduleActivity::class.java)
            Log.d("GroomingDebug", "Sending Cat ID: $catId")
            intent.putExtra("catid", catId)
            startActivity(intent)
        }

        // Vet visits button
        val vetBtn = findViewById<LinearLayout>(R.id.vetbtn)
        vetBtn.setOnClickListener {
            val intent = Intent(this, VetAppointmentCreatedActivity::class.java)
            Log.d("VetDebug", "Sending Cat ID: $catId")
            intent.putExtra("catid", catId)
            startActivity(intent)
        }

        // Photo Booth
        val photoBoothLayout = findViewById<LinearLayout>(R.id.layout_photo_booth)
        photoBoothLayout.setOnClickListener {
            val intent = Intent(this, PhotoBoothActivity::class.java)
            startActivity(intent)
        }

        // Health button → Weight Tracking
        val healthBtn = findViewById<LinearLayout>(R.id.healthbtn)
        healthBtn.setOnClickListener {
            val intent = Intent(this, WeightTrackingActivity::class.java)
            Log.d("HealthDebug", "Sending Cat ID: $catId")
            intent.putExtra("catid", catId)
            startActivity(intent)
        }

        // Edit button
        findViewById<ImageView>(R.id.ivEdit).setOnClickListener {
            // TODO: Open edit screen
        }

        // Delete button with confirmation dialog
        btnDelete.setOnClickListener {
            if (catId != -1) {
                AlertDialog.Builder(this)
                    .setTitle("Delete Profile")
                    .setMessage("Are you sure you want to delete this profile?")
                    .setPositiveButton("Yes") { dialog, _ ->
                        deletePetProfile(catId)
                        dialog.dismiss()
                    }
                    .setNegativeButton("No") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            } else {
                Toast.makeText(this, "Invalid cat ID", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Delete profile API
    private fun deletePetProfile(catId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = ApiClient.apiService.deletePet(catId)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body()?.isSuccess() == true) {
                        Toast.makeText(this@Page77, "Pet profile deleted", Toast.LENGTH_SHORT).show()
                        setResult(Activity.RESULT_OK)  // Refresh list on Page6
                        finish()
                    } else {
                        Toast.makeText(this@Page77, "Failed to delete profile", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@Page77, "Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
