package com.saveetha.myapp.ui.auth

data class DeleteResponse(
    val status: String,
    val message: String
) {
    fun isSuccess(): Boolean {
        return status.equals("success", ignoreCase = true)
    }
}
