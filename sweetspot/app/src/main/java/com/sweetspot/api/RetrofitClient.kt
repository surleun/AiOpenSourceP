package com.sweetspot.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private val retrofit = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:8080/")  // 실제 서버 주소
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // 기존 userApi, authApi 외에 homeApi 추가
    val homeApi: HomeApi = retrofit.create(HomeApi::class.java)
    val userApi: UserApi = retrofit.create(UserApi::class.java)
    val authApi: AuthApi = retrofit.create(AuthApi::class.java)
}