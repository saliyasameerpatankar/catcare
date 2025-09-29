package com.saveetha.myapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.saveetha.myapp.ui.auth.page2

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Immediately redirect to Login Page (page2)
        val intent = Intent(this, page2::class.java)
        startActivity(intent)
        finish()
    }
}
