package com.example.zim_android.data.model

data class DiaryResponse(
    val diaryId: Int,
    val userId: Int,
    val tripId: Int,
    val countryCode: String,
    val city: String,
    val dateTime: String,
    val content: String,
    val detailedLocation: String?,
    val audioUrl: String?,
    val emotionId: Int,
    val weatherId: Int,
    val images: List<DiaryImageResponse>
)

data class DiaryImageResponse(
    val imageId: Int,
    val imageUrl: String,
    val cameraType: String,     // "FRONT" or "BACK"
    val representative: Boolean
)