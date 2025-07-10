package com.example.zim_android.data.model

data class GeocodingResponse(
    val results: List<Result>
)

data class Result(
    val address_components: List<AddressComponent>
)

data class AddressComponent(
    val long_name: String,
    val types: List<String>
)