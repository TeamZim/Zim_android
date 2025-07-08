package com.example.zim_android.data.network

// 현재로서 자주 사용되는 사용자 정보를 어디서든 사용할 수 있게 싱글톤으로 선언함.
// 우선 임시로 1로 지정해둠
object UserSession {
    var userId: Int? = 1
    var currentTripId: Int? = 1

    fun clear() {
        userId = null
        currentTripId = null
    }
}