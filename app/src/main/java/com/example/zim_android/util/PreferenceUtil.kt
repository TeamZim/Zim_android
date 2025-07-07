package com.example.zim_android.util

import android.content.Context
import android.content.SharedPreferences

object PreferenceUtil {

    private const val PREF_NAME = "user_pref" // 실제로 생성될 XML 이름: user_pref.xml
    private const val KEY_USER_ID = "user_id"
    private const val KEY_ACCESS_TOKEN = "access_token"
    private const val KEY_NICKNAME = "nickname"

    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    // userId 저장 & 조회
    fun setUserId(context: Context, id: Int) {
        getPreferences(context).edit().putInt(KEY_USER_ID, id).apply()
    }

    fun getUserId(context: Context): Int {
        return getPreferences(context).getInt(KEY_USER_ID, -1) // 기본값 -1: 저장 안 돼있을 경우
    }
//
//    // access token 저장 & 조회
//    fun setAccessToken(context: Context, token: String) {
//        getPreferences(context).edit().putString(KEY_ACCESS_TOKEN, token).apply()
//    }
//
//    fun getAccessToken(context: Context): String? {
//        return getPreferences(context).getString(KEY_ACCESS_TOKEN, null)
//    }
//
//    // 닉네임 저장 & 조회 (예시 추가)
//    fun setNickname(context: Context, nickname: String) {
//        getPreferences(context).edit().putString(KEY_NICKNAME, nickname).apply()
//    }
//
//    fun getNickname(context: Context): String? {
//        return getPreferences(context).getString(KEY_NICKNAME, null)
//    }
//
//    // 로그아웃 등 전체 제거
//    fun clearAll(context: Context) {
//        getPreferences(context).edit().clear().apply()
//    }
}



// 사용 예시

//// userId 저장
//PreferenceUtil.setUserId(requireContext(), 1)
//
//// userId 불러오기
//val userId = PreferenceUtil.getUserId(requireContext())
//
//// access token 저장
//PreferenceUtil.setAccessToken(requireContext(), "Bearer abc123")
//
//// 전체 초기화 (로그아웃 시)
//PreferenceUtil.clearAll(requireContext())