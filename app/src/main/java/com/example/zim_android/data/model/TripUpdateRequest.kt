package com.example.zim_android.data.model

data class TripUpdateRequest (
    val tripName: String,
    val description: String,
    val themeId: Int,
    val representativeImageUrl: String,
    val startDate: String,
    val endDate: String
)