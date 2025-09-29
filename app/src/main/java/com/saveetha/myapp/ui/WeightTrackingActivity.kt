package com.saveetha.myapp.ui.health

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.saveetha.myapp.R
import com.saveetha.myapp.models.WeightEntry
import com.saveetha.myapp.network.ApiClient
import com.saveetha.myapp.models.WeightResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class WeightTrackingActivity : AppCompatActivity() {

    private lateinit var etWeight: EditText
    private lateinit var etNotes: EditText
    private lateinit var btnAddWeight: Button
    private lateinit var tvAlert: TextView
    private lateinit var weightGraph: WeightGraphView
    private lateinit var rvWeightHistory: RecyclerView

    private val weightList = mutableListOf<WeightEntry>()
    private lateinit var adapter: WeightAdapter
    private var catId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weight_tracking)

        etWeight = findViewById(R.id.etWeight)
        etNotes = findViewById(R.id.etNotes)
        btnAddWeight = findViewById(R.id.btnAddWeight)
        tvAlert = findViewById(R.id.tvAlert)
        weightGraph = findViewById(R.id.weightGraph)
        rvWeightHistory = findViewById(R.id.rvWeightHistory)

        rvWeightHistory.layoutManager = LinearLayoutManager(this)
        adapter = WeightAdapter(weightList)
        rvWeightHistory.adapter = adapter

        // ✅ Receive catId
        catId = intent.getIntExtra("catid", -1)
        if (catId == -1) {
            Toast.makeText(this, "❌ Cat ID missing!", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        btnAddWeight.setOnClickListener {
            addWeightEntry()
        }

        // ✅ Load weights when opening
        loadWeights()
    }

    private fun addWeightEntry() {
        val weightText = etWeight.text.toString()
        val notes = etNotes.text.toString()

        if (weightText.isEmpty()) {
            tvAlert.text = "Please enter a weight"
            tvAlert.visibility = TextView.VISIBLE
            return
        }

        val weight = weightText.toDouble()
        val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        val apiService = ApiClient.apiService
        apiService.addWeight(catId, date, weight, notes).enqueue(object : Callback<WeightResponse> {
            override fun onResponse(call: Call<WeightResponse>, response: Response<WeightResponse>) {
                val body = response.body()
                if (body?.status == "success") {
                    Toast.makeText(this@WeightTrackingActivity, "✅ Weight saved!", Toast.LENGTH_SHORT).show()
                    etWeight.text.clear()
                    etNotes.text.clear()
                    loadWeights() // refresh
                } else {
                    Toast.makeText(this@WeightTrackingActivity, body?.message ?: "Error saving weight", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<WeightResponse>, t: Throwable) {
                Toast.makeText(this@WeightTrackingActivity, "Failed: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun loadWeights() {
        val apiService = ApiClient.apiService
        // ✅ Fetch weights only for this cat
        apiService.getWeights(catId).enqueue(object : Callback<List<WeightEntry>> {
            override fun onResponse(call: Call<List<WeightEntry>>, response: Response<List<WeightEntry>>) {
                val weights = response.body() ?: emptyList()

                weightList.clear()
                weightList.addAll(weights.sortedByDescending { it.date })
                adapter.notifyDataSetChanged()

                // ✅ Update graph
                val graphData = weightList.map { it.weight }
                weightGraph.setData(graphData)

                checkWeightAlert()
            }

            override fun onFailure(call: Call<List<WeightEntry>>, t: Throwable) {
                Toast.makeText(this@WeightTrackingActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun checkWeightAlert() {
        if (weightList.size >= 2) {
            val latest = weightList[0].weight
            val previous = weightList[1].weight
            if (latest > previous + 1.0) {
                tvAlert.text = "⚠️ Sudden weight increase detected!"
                tvAlert.visibility = TextView.VISIBLE
            } else {
                tvAlert.visibility = TextView.GONE
            }
        }
    }
}
