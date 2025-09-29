package com.saveetha.myapp.ui.auth

data class SignupRequest(
    val email: String,
    val password: String,
    val firstname: String,
    val lastname: String
)
