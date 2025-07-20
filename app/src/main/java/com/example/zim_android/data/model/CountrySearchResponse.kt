package com.example.zim_android.data.model

data class CountrySearchResponse (
    val countryCode: String,
    val countryName: String,
    val emoji: String
) {
    override fun toString(): String {
        return countryName
    }

    override fun equals(other: Any?): Boolean {
        return other is CountrySearchResponse && other.countryName == this.countryName
    }

    override fun hashCode(): Int {
        return countryName.hashCode()
    }
}