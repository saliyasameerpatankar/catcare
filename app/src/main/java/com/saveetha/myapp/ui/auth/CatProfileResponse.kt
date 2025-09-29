package com.saveetha.myapp.ui.auth


data class CatProfile(
    val catid : Int,
    val catname: String,
    val age: Int,
    val breed: String,
    val photo: String? // nullable because API returns null sometimes
)

data class CatProfileResponse(
    val status: String,
    val profiles: List<CatProfile>
)
