package com.saveetha.myapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.saveetha.myapp.network.ApiClient
import com.saveetha.myapp.ui.adapters.CatAdapter
import com.saveetha.myapp.ui.auth.CatProfileResponse
import com.saveetha.myapp.ui.auth.Page77
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Page6 : AppCompatActivity() {

    private lateinit var tvWelcome: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var btnAddNewPet: Button
    private lateinit var emptyStateView: LinearLayout
    private lateinit var catAdapter: CatAdapter
    private lateinit var pageableLauncher: ActivityResultLauncher<Intent>

    private var userId: Int = -1

    companion object {
        private const val REQUEST_ADD_OR_EDIT_CAT = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_page6)

        tvWelcome = findViewById(R.id.tvWelcome)
        recyclerView = findViewById(R.id.recyclecat)
        btnAddNewPet = findViewById(R.id.btnAddNewPet)
        emptyStateView = findViewById(R.id.emptyStateView)

        recyclerView.layoutManager = LinearLayoutManager(this)

        catAdapter = CatAdapter(this, mutableListOf()) { cat ->
            val intent = Intent(this, Page77::class.java).apply {
                putExtra("catid", cat.catid)
                putExtra("catName", cat.catname)
                putExtra("catBreed", cat.breed)
                putExtra("catAge", cat.age.toString())
                putExtra("catPhoto", cat.photo)
            }
            pageableLauncher.launch(intent)
        }
        recyclerView.adapter = catAdapter

        userId = intent.getIntExtra("userId", -1)
        Log.d("Page6", "UserId received in Page6: $userId")

        if (userId == -1) {
            Toast.makeText(this, "User ID not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        fetchUserHome(userId)

        pageableLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                // Refresh the cat profiles list after add/edit/delete
                fetchUserHome(userId)
            }
        }

        btnAddNewPet.setOnClickListener {
            val intent = Intent(this, Page5::class.java)
            intent.putExtra("userId", userId)
            pageableLauncher.launch(intent)
        }
    }

    private fun fetchUserHome(userId: Int) {
        ApiClient.apiService.getCatProfiles(userId).enqueue(object : Callback<CatProfileResponse> {
            override fun onResponse(call: Call<CatProfileResponse>, response: Response<CatProfileResponse>) {
                if (response.isSuccessful) {
                    val catResponse = response.body()

                    if (catResponse?.status == "success" && !catResponse.profiles.isNullOrEmpty()) {
                        tvWelcome.text = "Cat Profiles"
                        recyclerView.visibility = View.VISIBLE
                        emptyStateView.visibility = View.GONE
                        catAdapter.updateCats(catResponse.profiles.toMutableList())
                    } else {
                        recyclerView.visibility = View.GONE
                        emptyStateView.visibility = View.VISIBLE
                        catAdapter.updateCats(mutableListOf())
                    }
                } else {
                    recyclerView.visibility = View.GONE
                    emptyStateView.visibility = View.VISIBLE
                    Toast.makeText(this@Page6, "Server error", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<CatProfileResponse>, t: Throwable) {
                recyclerView.visibility = View.GONE
                emptyStateView.visibility = View.VISIBLE
                Toast.makeText(this@Page6, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}