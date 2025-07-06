package com.example.zim_android.data.model

data  class UserResponse (
    val userId: Int,
    val kakaoId: String,
    val profileImageUrl: String,
    val surName: String,
    val firstName: String,
    val koreanName: String,
    val birth: String,
    val nationality: String,
    val diaryCount: Int,
    val visitedCountryCount: Int,
    val flags: String?
    )