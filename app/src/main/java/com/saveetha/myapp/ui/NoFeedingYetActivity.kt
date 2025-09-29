package com.saveetha.myapp.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.saveetha.myapp.R

class NoFeedingYetActivity : AppCompatActivity() {

    private lateinit var topAppBar: MaterialToolbar
    private lateinit var addScheduleBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.nofeedingyet)

        // Initialize views
        topAppBar = findViewById(R.id.topAppBar)
        addScheduleBtn = findViewById(R.id.addScheduleBtn)

        // Handle back arrow click in the top app bar
        topAppBar.setNavigationOnClickListener {
            onBackPressed()
        }

        // Handle "Add Feeding Schedule" button click
        addScheduleBtn.setOnClickListener {
            val intent = Intent(this, AddFeedingScheduleActivity::class.java)
            startActivity(intent)
        }
    }
}
