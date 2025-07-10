package com.example.zim_android.data.model

import com.example.zim_android.data.network.DiaryTempStore
import com.example.zim_android.data.network.DiaryTempStore.audioUrl
import com.example.zim_android.data.network.DiaryTempStore.city
import com.example.zim_android.data.network.DiaryTempStore.content
import com.example.zim_android.data.network.DiaryTempStore.countryCode
import com.example.zim_android.data.network.DiaryTempStore.dateTime
import com.example.zim_android.data.network.DiaryTempStore.detailedLocation
import com.example.zim_android.data.network.DiaryTempStore.emotionId
import com.example.zim_android.data.network.DiaryTempStore.images
import com.example.zim_android.data.network.DiaryTempStore.tripId
import com.example.zim_android.data.network.DiaryTempStore.weatherId

data class TripCreateRequest (
    val tripName: String,
    val description: String,
    val themeId: Int,
    val userId: Int
) {
    override fun toString(): String {
        return """
        tripName = $tripName
        description = $description
        themeId = $themeId
        userId = $userId
    """.trimIndent()
    }
}