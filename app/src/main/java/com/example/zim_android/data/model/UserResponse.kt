package com.example.zim_android.data.model
data class UserResponse(
    val user: User,
    val statistics: Statistics,
    val flags: String?
)

data class User(
    val userId: Int,
    val kakaoId: String? = null, // 서버에서 내려올 경우에 대비
    val profileImageUrl: String,
    val surName: String,
    val firstName: String,
    val koreanName: String,
    val birth: String,
    val nationality: String
)

data class Statistics(
    val diaryCount: Int,
    val countryCount: Int
)