package com.example.zim_android.data.model

data class TripResponse (
    val id: Int,
    val tripName: String,
    val description: String,
    val createdAt: String,
    val startDate: String,
    val endDate: String,
    val isDeleted: Boolean,
    val representativeImageUrl: String?,
    val userId: Int,
    val userKakaoId: String,
    val userKoreanName: String,
    val themeId: Int,
    val themeName: String,
    val diaryCount: Int,
    )