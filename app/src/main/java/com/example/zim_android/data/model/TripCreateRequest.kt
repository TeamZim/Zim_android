package com.example.zim_android.data.model

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