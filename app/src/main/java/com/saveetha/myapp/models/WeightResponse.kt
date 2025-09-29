package com.saveetha.myapp.models

data class WeightResponse(
    val status: String,
    val message: String?,
    val data: WeightEntry?   // backend sends a single entry on POST
)
