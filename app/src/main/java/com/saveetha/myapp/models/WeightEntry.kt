package com.saveetha.myapp.models

data class WeightEntry(
    val id: Int,
    val catid: Int,
    val date: String,
    val weight: Double,
    val notes: String?
)
