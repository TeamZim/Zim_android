package com.example.zim_android.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TripResponse(
    val id: Int,
    val tripName: String,
    val description: String,
    val startDate: String,
    val endDate: String,
    val isDeleted: Boolean,
    val representativeImageUrl: String?,
    val userId: Int,
    val userKakaoId: String,
    val userKoreanName: String,
    val themeId: Int,
    val themeName: String,
    val themeSampleImageUrl: String,
    val themeCardImageUrl: String,
    val diaryCount: Int
) : Parcelable