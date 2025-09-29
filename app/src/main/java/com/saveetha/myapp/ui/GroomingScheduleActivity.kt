package com.saveetha.myapp.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.saveetha.myapp.R
import com.saveetha.myapp.network.ApiClient
import com.saveetha.myapp.page12
import com.saveetha.myapp.ui.adapters.GroomingAdapter
import com.saveetha.myapp.ui.auth.GroomingTaskResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GroomingScheduleActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: GroomingAdapter
    private lateinit var emptyView: View
    private lateinit var taskListView: View
    private var catId: Int = -1

    // ✅ Register ActivityResultLauncher
    private val addTaskLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            // Refresh the list after a new task is added
            fetchTasks(catId)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.groomingschedulecreated)

        recyclerView = findViewById(R.id.recyclegroom)
        emptyView = findViewById(R.id.emptyLayout)
        taskListView = findViewById(R.id.taskListLayout)

        recyclerView.layoutManager = LinearLayoutManager(this)

        catId = intent.getIntExtra("catid", -1)

        if (catId != -1) {
            Log.d("GroomingDebug", "Received Cat ID: $catId")
            fetchTasks(catId)
        } else {
            Toast.makeText(this, "Cat ID missing", Toast.LENGTH_SHORT).show()
            showEmptyLayout()
        }

        val addgroomingBtn = findViewById<Button>(R.id.btnAddGroomingnew)
        addgroomingBtn.setOnClickListener {
            val intent = Intent(this, page12::class.java)
            intent.putExtra("catid", catId)
            addTaskLauncher.launch(intent) // launch with result
        }
    }

    private fun fetchTasks(catid: Int) {
        ApiClient.apiService.getGroomingTasks(catid).enqueue(object : Callback<GroomingTaskResponse> {
            override fun onResponse(
                call: Call<GroomingTaskResponse>,
                response: Response<GroomingTaskResponse>
            ) {
                Log.d("GroomingDebug", "Cat ID Used in API: $catid")
                Log.d("GroomingDebug", "Response success: ${response.isSuccessful}")

                if (response.isSuccessful && response.body()?.status == "success") {
                    val taskList = response.body()?.data ?: emptyList()
                    if (taskList.isNotEmpty()) {
                        showTaskList(taskList)
                    } else {
                        showEmptyLayout()
                    }
                } else {
                    showEmptyLayout()
                    Toast.makeText(this@GroomingScheduleActivity, "No tasks found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<GroomingTaskResponse>, t: Throwable) {
                showEmptyLayout()
                Toast.makeText(this@GroomingScheduleActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showTaskList(taskList: List<com.saveetha.myapp.ui.auth.GroomingTask>) {
        taskListView.visibility = View.VISIBLE
        emptyView.visibility = View.GONE

        adapter = GroomingAdapter(taskList) { taskName, isChecked ->
            Toast.makeText(
                this@GroomingScheduleActivity,
                "$taskName is ${if (isChecked) "Enabled" else "Disabled"}",
                Toast.LENGTH_SHORT
            ).show()
        }
        recyclerView.adapter = adapter
    }

    private fun showEmptyLayout() {
        taskListView.visibility = View.GONE
        emptyView.visibility = View.VISIBLE
    }
}
