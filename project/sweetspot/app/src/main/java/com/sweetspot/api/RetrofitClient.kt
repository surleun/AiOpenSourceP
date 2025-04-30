package com.sweetspot.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:8080/"
    // 에뮬레이터에서는 localhost 대신 10.0.2.2 써야 해! (물리 디바이스면 서버 IP 입력)

    val api: SweetSpotApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SweetSpotApi::class.java)
    }
}