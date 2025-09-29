package com.saveetha.myapp.ui.auth

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.saveetha.myapp.R
import com.saveetha.myapp.network.ApiClient
import com.saveetha.myapp.network.VetAppointmentResponse
import com.saveetha.myapp.network.VetAppointment
import com.saveetha.myapp.ui.VetVisitCreatingActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VetAppointmentCreatedActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyStateLayout: LinearLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var addButton: Button

    private lateinit var adapter: VetVisitAdapter
    private var catId: Int = -1
    private var petName: String = ""
    private var petBreed: String = ""
    private var petAge: String = ""
    private var petPhoto: String = ""

    private val addAppointmentLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                loadAppointments()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.vetappointmentcreated)

        // ✅ Get pet details from intent
        catId = intent.getIntExtra("catid", -1)
        petName = intent.getStringExtra("catName") ?: "My Cat"
        petBreed = intent.getStringExtra("catBreed") ?: "Unknown Breed"
        petAge = intent.getStringExtra("catAge") ?: "Unknown Age"
        petPhoto = intent.getStringExtra("catPhoto") ?: ""

        recyclerView = findViewById(R.id.vetrecycler)
        emptyStateLayout = findViewById(R.id.layout_empty_state)
        progressBar = findViewById(R.id.progressBar)
        addButton = findViewById(R.id.button_add_vet_visit)

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = VetVisitAdapter(emptyList(), petName)
        recyclerView.adapter = adapter

        loadAppointments()

        addButton.setOnClickListener {
            val intent = Intent(this, VetVisitCreatingActivity::class.java).apply {
                putExtra("catid", catId)
                putExtra("catName", petName)
                putExtra("catBreed", petBreed)
                putExtra("catAge", petAge)
                putExtra("catPhoto", petPhoto)
            }
            addAppointmentLauncher.launch(intent)
        }
    }

    private fun loadAppointments() {
        progressBar.visibility = View.VISIBLE

        ApiClient.apiService.getAppointments(catId)
            .enqueue(object : Callback<VetAppointmentResponse> {
                override fun onResponse(call: Call<VetAppointmentResponse>, response: Response<VetAppointmentResponse>) {
                    progressBar.visibility = View.GONE
                    val body = response.body()
                    if (response.isSuccessful && body?.status == "success") {
                        val appointments = body.data
                        if (appointments.isNotEmpty()) {
                            showList(appointments)
                        } else {
                            showEmpty()
                        }
                    } else {
                        showEmpty()
                    }
                }

                override fun onFailure(call: Call<VetAppointmentResponse>, t: Throwable) {
                    progressBar.visibility = View.GONE
                    showEmpty()
                }
            })
    }

    private fun showList(appointments: List<VetAppointment>) {
        emptyStateLayout.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
        adapter.updateData(appointments)
    }

    private fun showEmpty() {
        emptyStateLayout.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
    }
}
