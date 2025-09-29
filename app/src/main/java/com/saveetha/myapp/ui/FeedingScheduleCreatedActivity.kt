package com.saveetha.myapp.ui

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.saveetha.myapp.R

class FeedingScheduleCreatedActivity : AppCompatActivity() {

    private lateinit var backArrow: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.feedingschedulecreated) // XML layout

        // Initialize views
        backArrow = findViewById(R.id.backArrow)

        // Handle back navigation
        backArrow.setOnClickListener {
            onBackPressed() // or finish()
        }

        // TODO: Add more logic if needed (e.g., show saved feeding info)
    }
}
