package com.saveetha.myapp.models

data class CatProfileUploadResponse(
    val status: String,
    val message: String,
    val data: CatProfile?
)

data class CatProfile(
    val catid: Int,
    val userid: Int,
    val catname: String,
    val age: String,
    val breed: String,
    val gender: String,
    val photo: String?
)
