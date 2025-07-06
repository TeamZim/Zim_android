package com.example.zim_android.data.model

data class UpdateDiaryOptionalFieldsRequest(
    val detailedLocation: String,
    val audioUrl: String,
    val emotionId: Int,
    val weatherId: Int
)