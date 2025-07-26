package com.example.zim_android.data.network

import android.content.Context

// 현재로서 자주 사용되는 사용자 정보를 어디서든 사용할 수 있게 싱글톤으로 선언함.
// 우선 임시로 1로 지정해둠
object UserSession {
    val userId: Int = 3
    var currentTripId: Int? = 1

    fun saveToPreferences(context: Context) {
        val prefs = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        prefs.edit().putInt("userId", userId ?: -1).apply()
    }
    fun loadFromPreferences(context: Context) {
        val prefs = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)

        val savedId = prefs.getInt("userId", 3)
    }
//    fun clear(context: Context) {
//        userId = null
//        currentTripId = null
//        val prefs = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
//        prefs.edit().clear().apply()
//    }

}



//    fun clear() {
//        userId = null
//        currentTripId = null
//    }
