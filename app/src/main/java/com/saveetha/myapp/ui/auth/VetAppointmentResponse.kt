package com.saveetha.myapp.network

// Whole API response
data class VetAppointmentResponse(
    val status: String,
    val data: List<VetAppointment>
)

// Single appointment item
data class VetAppointment(
    val appointmentid: Int,
    val catid: Int,
    val catname: String,
    val cat_breed: String,
    val cat_age: String,
    val cat_photo: String, // URL or file path
    val next_appointment_date: String,
    val next_appointment_time: String,
    val reason_or_visit_type: String,
    val clinic_name: String,
    val vet_name: String,
    val vet_phone_number: String,
    val last_visit_date: String
)


