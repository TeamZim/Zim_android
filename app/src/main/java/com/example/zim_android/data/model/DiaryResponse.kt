package com.example.zim_android.data.model

data class DiaryResponse(
    val id: Int,
    val tripId: Int,
    val tripName: String,
    val countryName: String,
    val city: String,
    val dateTime: String,
    val createdAt: String,
    val content: String,
    val detailedLocation: String?,
    val audioUrl: String?,
    val emotionColor: String, // e.g. "#FF6B6B"
    val weather: String,      // e.g. "맑음"
    val images: List<DiaryImageResponse>
)

data class DiaryImageResponse(
    val id: Int,
    val imageUrl: String,
    val cameraType: String,     // "FRONT" or "BACK"
    val isRepresentative: Boolean,
    val imageOrder: Int
)
