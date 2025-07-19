package com.example.zim_android.data.model

data class DiaryCreateRequest (
    val userId: Int,
    val tripId: Int,
    val countryCode: String,
    val city: String,
    val dateTime: String,
    val content: String,
    val images: List<DiaryImageRequest>,
    val detailedLocation: String,
    val audioUrl: String,
    val emotionId: Int,
    val weatherId: Int?
)

data class DiaryImageRequest(
    val imageUrl: String,
    val cameraType: String,
    val isRepresentative: Boolean
)