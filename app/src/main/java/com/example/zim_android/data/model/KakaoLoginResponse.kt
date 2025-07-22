package com.example.zim_android.data.model

data  class KakaoLoginResponse (
    val userId: Int,
    val registered: Boolean,
    val kakaoId: String,
    val profileImageUrl: String
)