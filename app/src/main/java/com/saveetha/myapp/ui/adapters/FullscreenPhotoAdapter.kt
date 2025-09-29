package com.saveetha.myapp.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.saveetha.myapp.R
import com.saveetha.myapp.models.Photo
import java.io.File

class FullscreenPhotoAdapter(
    private val photos: List<Photo>
) : RecyclerView.Adapter<FullscreenPhotoAdapter.PhotoViewHolder>() {

    inner class PhotoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageViewFullscreen)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_fullscreen_photo, parent, false)
        return PhotoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val photo = photos[position]

        Glide.with(holder.itemView.context)
            .load(File(photo.url))   // load from internal storage
            .fitCenter()
            .placeholder(R.drawable.paw)
            .into(holder.imageView)
    }

    override fun getItemCount(): Int = photos.size
}
