package com.saveetha.myapp.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.saveetha.myapp.R
import com.saveetha.myapp.ui.auth.GroomingTask

class GroomingAdapter(
    private val taskList: List<GroomingTask>,
    private val onToggle: (taskName: String, isChecked: Boolean) -> Unit
) : RecyclerView.Adapter<GroomingAdapter.TaskViewHolder>() {

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val taskTitle: TextView = itemView.findViewById(R.id.taskTitleTextView)
        val frequency: TextView = itemView.findViewById(R.id.frequencyTextView)
        val lastDone: TextView = itemView.findViewById(R.id.lastDoneTextView)
        val nextDue: TextView = itemView.findViewById(R.id.nextDueTextView)
        val taskSwitch: Switch = itemView.findViewById(R.id.taskSwitch)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.taskrecycler, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = taskList[position]
        holder.taskTitle.text = task.task_name
        holder.frequency.text = "Frequency: ${task.frequency}"
        holder.lastDone.text = "Last Done: ${task.last_done}"
        holder.nextDue.text = "Next Due: ${task.next_due}"

        holder.taskSwitch.setOnCheckedChangeListener(null) // Reset listener before re-assigning
        holder.taskSwitch.setOnCheckedChangeListener { _, isChecked ->
            onToggle(task.task_name, isChecked)
        }
    }

    override fun getItemCount(): Int = taskList.size
}
