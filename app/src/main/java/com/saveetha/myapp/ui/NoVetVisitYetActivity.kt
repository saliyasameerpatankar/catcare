package com.saveetha.myapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.saveetha.myapp.ui.VetVisitCreatingActivity

class NoVetVisitYetActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageView
    private lateinit var btnAdd: ImageView
    private lateinit var createProfileButton: Button
    private lateinit var dynamicContentContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.novetvisityet)

        setupSystemBarInsets()
        initViews()
        setupListeners()
    }

    private fun setupSystemBarInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )
            WindowInsetsCompat.CONSUMED
        }
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBack)
        btnAdd = findViewById(R.id.btnAdd)
        createProfileButton = findViewById(R.id.create_profilebutton)
        dynamicContentContainer = findViewById(R.id.dynamic_content_container)
    }

    private fun setupListeners() {
        btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val openVetVisitActivity = View.OnClickListener {
            startActivity(Intent(this, VetVisitCreatingActivity::class.java))
        }

        btnAdd.setOnClickListener(openVetVisitActivity)
        createProfileButton.setOnClickListener(openVetVisitActivity)
    }
}
