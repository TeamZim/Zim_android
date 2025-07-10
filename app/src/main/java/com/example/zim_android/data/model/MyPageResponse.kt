package com.example.zim_android.data.model

data class MyPageResponse(
    val user: UserInfo,
    val statistics: MyStatistics,
    val flags: String
)

data class UserInfo(
    val userId: Int,
    val profileImageUrl: String,
    val surName: String,
    val firstName: String,
    val koreanName: String,
    val birth: String,
    val nationality: String
)

data class MyStatistics(
    val countryCount: Int,
    val diaryCount: Int
)