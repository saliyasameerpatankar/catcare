package com.saveetha.myapp.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.saveetha.myapp.R
import com.saveetha.myapp.models.Photo
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider

class PhotoBoothActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var fabCamera: FloatingActionButton
    private lateinit var adapter: PhotoAdapter
    private lateinit var photos: MutableList<Photo>

    private var catId: Int = -1
    private var tempPhotoPath: String? = null   // store latest capture path

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_booth)

        catId = intent.getIntExtra("catid", -1)
        val catName = intent.getStringExtra("catname") ?: "Photo Booth"

        supportActionBar?.title = "$catName's Photos"

        recyclerView = findViewById(R.id.recyclerViewPhotos)
        fabCamera = findViewById(R.id.fabCamera)

        photos = PhotoStorage.getPhotosForCat(this, catId).toMutableList()

        adapter = PhotoAdapter(
            photos,
            onClick = { pos -> showFullPhoto(photos[pos].url) },
            onLongClick = { pos -> showOptions(pos) }
        )

        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.adapter = adapter

        fabCamera.setOnClickListener { openCamera() }
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        // ✅ Create file in app external storage (PhotoBooth/)
        val photoFile = File(
            getExternalFilesDir("PhotoBooth"),
            "IMG_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())}.jpg"
        )

        tempPhotoPath = photoFile.absolutePath

        val uri = FileProvider.getUriForFile(
            this,
            "${packageName}.fileprovider",
            photoFile
        )

        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        startActivityForResult(intent, 1001)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1001 && resultCode == RESULT_OK) {
            tempPhotoPath?.let { path ->
                val file = File(path)
                if (file.exists()) {
                    val newPhoto = Photo(file.absolutePath)
                    photos.add(newPhoto)
                    adapter.notifyItemInserted(photos.size - 1)
                    PhotoStorage.savePhotosForCat(this, catId, photos)
                }
            }
        }
    }

    private fun showFullPhoto(url: String) {
        val intent = Intent(this, FullscreenPhotoActivity::class.java)
        intent.putExtra("photos", ArrayList(photos))
        intent.putExtra("position", photos.indexOfFirst { it.url == url })
        startActivity(intent)
    }

    private fun showOptions(position: Int) {
        val photo = photos[position]
        val options = arrayOf("Delete", "Share")

        AlertDialog.Builder(this)
            .setItems(options) { _, which ->
                when (which) {
                    0 -> { // Delete
                        photos.removeAt(position)
                        adapter.notifyItemRemoved(position)
                        PhotoStorage.savePhotosForCat(this, catId, photos)
                        // Optional: also delete file from storage
                        File(photo.url).delete()
                    }
                    1 -> { // Share (✅ Fixed with FileProvider)
                        val file = File(photo.url)
                        val uri = FileProvider.getUriForFile(
                            this,
                            "${packageName}.fileprovider", // must match manifest authority
                            file
                        )

                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "image/*"
                            putExtra(Intent.EXTRA_STREAM, uri)
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }
                        startActivity(Intent.createChooser(intent, "Share Photo"))
                    }
                }
            }
            .show()
    }
}
