package com.saveetha.myapp.ui.health

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.saveetha.myapp.R
import com.saveetha.myapp.models.WeightEntry

class WeightAdapter(private val items: List<WeightEntry>) :
    RecyclerView.Adapter<WeightAdapter.WeightViewHolder>() {

    class WeightViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        val tvWeight: TextView = itemView.findViewById(R.id.tvWeight)
        val tvNotes: TextView = itemView.findViewById(R.id.tvNotes)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeightViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_weight_entry, parent, false)
        return WeightViewHolder(view)
    }

    override fun onBindViewHolder(holder: WeightViewHolder, position: Int) {
        val item = items[position]
        holder.tvDate.text = item.date
        holder.tvWeight.text = "${item.weight} kg"
        holder.tvNotes.text = if (!item.notes.isNullOrEmpty()) item.notes else "-"

    }

    override fun getItemCount(): Int = items.size
}
