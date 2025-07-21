package com.example.zim_android

import android.content.Context

object PreferenceManager {
    private const val PREF_NAME = "my_preferences"
    private const val ONBOARDING_KEY = "onboarding_shown"

    fun isOnboardingShown(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(ONBOARDING_KEY, false)  // 기본값 false
    }

    fun setOnboardingShown(context: Context) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(ONBOARDING_KEY, true).apply()
    }
}

//    //개발용 온보딩 항상 보이게
//    //온보딩을 항상 안 봤다고 생각하게 만드는 법
//    fun isOnboardingShown(context: Context): Boolean {
//        return false // 항상 온보딩 보이게 만들기
//    }




