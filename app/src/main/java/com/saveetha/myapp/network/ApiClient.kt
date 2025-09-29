package com.saveetha.myapp.network

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    // Set this correctly in AppConfig (e.g. "http://172.23.53.39/catcare/")
    private const val BASE_URL = AppConfig.BASE_URL

    val retrofit: Retrofit by lazy {
        val gson = GsonBuilder()
            .setLenient() // handles malformed JSON
            .create()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}
