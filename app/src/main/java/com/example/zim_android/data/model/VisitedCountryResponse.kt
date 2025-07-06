package com.example.zim_android.data.model

data class VisitedCountryResponse (
    val visitedCountryId: Int,
    val countryCode: String,
    val countryName: String,
    val emoji: String,
    val emotionName: String,
    val color: String,
    val userId: Int
)