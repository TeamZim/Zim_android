package com.example.zim_android.data.network

import com.example.zim_android.data.model.GeocodingResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface GoogleGeocodingApi {
    @GET("geocode/json")
    suspend fun getAddressFromLocation(
        @Query("latlng") latlng: String,
        @Query("key") apiKey: String
    ): Response<GeocodingResponse>
}