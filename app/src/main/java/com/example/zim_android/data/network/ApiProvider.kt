package com.example.zim_android.data.network

object ApiProvider {
    // by lazy → 최초 호출 시 한 번만 생성되고 이후 재사용됨
    val api: ApiService by lazy {
        RetrofitClient.instance.create(ApiService::class.java)
    }
}