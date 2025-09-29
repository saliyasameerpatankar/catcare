package com.saveetha.myapp.ui.auth

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.saveetha.myapp.R
import com.saveetha.myapp.network.VetAppointment

class VetVisitAdapter(
    private var appointments: List<VetAppointment>,
    private val petName: String
) : RecyclerView.Adapter<VetVisitAdapter.VetViewHolder>() {

    class VetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imagePet: ImageView = itemView.findViewById(R.id.image_pet)
        val textPetName: TextView = itemView.findViewById(R.id.text_pet_name)
        val textCatBreed: TextView = itemView.findViewById(R.id.text_cat_breed)
        val textCatAge: TextView = itemView.findViewById(R.id.text_cat_age)
        val textAppointmentDateTime: TextView = itemView.findViewById(R.id.textappointmentdatetime)
        val textVisitType: TextView = itemView.findViewById(R.id.text_visit_type)
        val textClinicName: TextView = itemView.findViewById(R.id.text_clinic_name)
        val textVetName: TextView = itemView.findViewById(R.id.text_vet_name)
        val textPhone: TextView = itemView.findViewById(R.id.text_phone)
        val textLastVisit: TextView = itemView.findViewById(R.id.text_last_visit)
        val switchReminder: Switch = itemView.findViewById(R.id.switchbtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VetViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.vetrecycler, parent, false)
        return VetViewHolder(view)
    }

    override fun onBindViewHolder(holder: VetViewHolder, position: Int) {
        val appointment = appointments[position]

        // Load pet photo dynamically using Glide
        Glide.with(holder.imagePet.context)
            .load(appointment.cat_photo) // ✅ cat_photo is a URL or file path
            .placeholder(R.drawable.luna) // Fallback placeholder
            .into(holder.imagePet)

        // Basic info
        holder.textPetName.text = appointment.catname
        holder.textCatBreed.text = "Breed: ${appointment.cat_breed}"
        holder.textCatAge.text = "Age: ${appointment.cat_age}"

        // Appointment details
        holder.textAppointmentDateTime.text =
            "Next Appointment: ${appointment.next_appointment_date} at ${appointment.next_appointment_time}"

        holder.textVisitType.text = "Visit Type: ${appointment.reason_or_visit_type}"
        holder.textClinicName.text = "Clinic: ${appointment.clinic_name}"
        holder.textVetName.text = "Vet: ${appointment.vet_name}"
        holder.textPhone.text = "Phone: ${appointment.vet_phone_number}"
        holder.textLastVisit.text = "Last Visit: ${appointment.last_visit_date}"

        // Reminder switch (static for now)
        holder.switchReminder.isChecked = false
    }

    override fun getItemCount(): Int = appointments.size

    fun updateData(newAppointments: List<VetAppointment>) {
        appointments = newAppointments
        notifyDataSetChanged()
    }
}
