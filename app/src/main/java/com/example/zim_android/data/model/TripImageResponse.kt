package com.example.zim_android.data.model

data class TripImageResponse (
    val diaryId: Int,
    val imageUrl: String,
    val countryCode: String,
    val countryName: String,
    val city: String,
    val content: String
)