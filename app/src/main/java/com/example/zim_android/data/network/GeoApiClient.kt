package com.example.zim_android.data.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object GeoApiClient {
    val retrofit: GoogleGeocodingApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/maps/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GoogleGeocodingApi::class.java)
    }
}