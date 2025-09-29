package com.saveetha.myapp.ui.auth

data class SignupResponse(
    val status: String,        // e.g. "success" or "error"
    val message: String        // e.g. "Account created successfully" or error details
)
