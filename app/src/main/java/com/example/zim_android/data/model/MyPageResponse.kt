package com.example.zim_android.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MyPageResponse(
    val user: UserInfo,
    val statistics: MyStatistics,
    val flags: String
) : Parcelable

@Parcelize
data class UserInfo(
    val userId: Int,
    val profileImageUrl: String,
    val surName: String,
    val firstName: String,
    val koreanName: String,
    val birth: String,
    val nationality: String
) : Parcelable

@Parcelize
data class MyStatistics(
    val countryCount: Int,
    val diaryCount: Int
) : Parcelable
