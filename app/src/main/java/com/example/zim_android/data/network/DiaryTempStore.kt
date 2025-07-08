package com.example.zim_android.data.network

import com.example.zim_android.data.model.DiaryImageRequest

// 일기 생성에 있어 여러 액티비티 혹은 프래그먼트에서 정보를 받아와야해서 싱글톤으로 생성함.
object DiaryTempStore {
    var userId: Int? = null
    var tripId: Int? = null
    var countryCode: String = ""
    var city: String = ""
    var dateTime: String = ""
    var content: String = ""
    var images: List<DiaryImageRequest> = emptyList()
    var detailedLocation: String = ""
    var audioUrl: String = ""
    var emotionId: Int? = null
    var weatherId: Int? = null

    fun clear() {
        userId = null
        tripId = null
        countryCode = ""
        city = ""
        dateTime = ""
        content = ""
        images = emptyList()
        detailedLocation = ""
        audioUrl = ""
        emotionId = null
        weatherId = null
    }

    override fun toString(): String {
        return """
        userId = $userId
        tripId = $tripId
        countryCode = $countryCode
        city = $city
        dateTime = $dateTime
        content = $content
        images = $images
        detailedLocation = $detailedLocation
        audioUrl = $audioUrl
        emotionId = $emotionId
        weatherId = $weatherId
    """.trimIndent()
    }
}