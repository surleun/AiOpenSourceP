package com.sweetspot.api

import retrofit2.Call
import retrofit2.http.GET
import com.sweetspot.api.MessageResponse   // 이미 선언된 모델을 import

interface HomeApi {
    @GET("/api/home")  // 실제 경로에 맞게 수정
    fun getHome(): Call<MessageResponse>
}