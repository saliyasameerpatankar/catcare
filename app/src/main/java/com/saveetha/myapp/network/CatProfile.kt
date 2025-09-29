package com.saveetha.myapp.network

data class CatProfile(
    val catid: Int,
    val catname: String,
    val breed: String,
    val age: Int,
    val photo: String? // nullable because sometimes no photo is returned
)
