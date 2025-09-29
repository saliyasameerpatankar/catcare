package com.saveetha.myapp.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.saveetha.myapp.R
import com.saveetha.myapp.ui.auth.CatProfile

class CatAdapter(
    private val context: Context,
    private val catList: MutableList<CatProfile>,   // ✅ MutableList kept inside
    private val onCatClick: (CatProfile) -> Unit
) : RecyclerView.Adapter<CatAdapter.CatViewHolder>() {

    class CatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageViewCat: ImageView = itemView.findViewById(R.id.imageViewCat)
        val textViewName: TextView = itemView.findViewById(R.id.textViewName)
        val textViewBreed: TextView = itemView.findViewById(R.id.textViewBreed)
        val textViewAge: TextView = itemView.findViewById(R.id.textViewAge)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CatViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.catrecycler, parent, false)
        return CatViewHolder(view)
    }

    override fun onBindViewHolder(holder: CatViewHolder, position: Int) {
        val cat = catList[position]

        holder.textViewName.text = cat.catname
        holder.textViewBreed.text = cat.breed
        holder.textViewAge.text = "${cat.age} years old"

        if (!cat.photo.isNullOrEmpty()) {
            Glide.with(context)
                .load(cat.photo)
                .placeholder(R.drawable.luna) // fallback image
                .into(holder.imageViewCat)
        } else {
            holder.imageViewCat.setImageResource(R.drawable.luna)
        }

        holder.itemView.setOnClickListener {
            onCatClick(cat)
        }
    }

    override fun getItemCount(): Int = catList.size

    // ✅ Remove cat instantly
    fun removeCat(catid: Int) {
        val index = catList.indexOfFirst { it.catid == catid }
        if (index != -1) {
            catList.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    // ✅ Update entire list (used when refetching from server)
    fun updateCats(newCats: List<CatProfile>) {
        catList.clear()
        catList.addAll(newCats)
        notifyDataSetChanged()
    }
}
