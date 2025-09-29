package com.saveetha.myapp.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.saveetha.myapp.R
import com.saveetha.myapp.models.Photo

class FullscreenPhotoActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var adapter: FullscreenPhotoAdapter
    private var photos: ArrayList<Photo> = arrayListOf()
    private var startPosition: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // ✅ Use the correct layout
        setContentView(R.layout.activity_fullscreen_photo)

        // ✅ Correct ID
        viewPager = findViewById(R.id.viewPager)

        // ✅ Receive photos
        photos = intent.getSerializableExtra("photos") as? ArrayList<Photo> ?: arrayListOf()
        startPosition = intent.getIntExtra("position", 0)

        adapter = FullscreenPhotoAdapter(photos)
        viewPager.adapter = adapter
        viewPager.setCurrentItem(startPosition, false)
    }
}
