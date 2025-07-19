package com.example.zim_android

import android.content.Context

object PreferenceManager {
    private const val PREF_NAME = "my_preferences"
    private const val ONBOARDING_KEY = "onboarding_shown"

    fun isOnboardingShown(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(ONBOARDING_KEY, false)
    }

    fun setOnboardingShown(context: Context) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(ONBOARDING_KEY, true).apply()
    }
}
