package com.sweetspot.api

import retrofit2.Call
import retrofit2.http.GET

data class MessageResponse(val message: String)

interface SweetSpotApi {
    @GET("/") // 서버 기본 주소에 GET 요청
    fun getHome(): Call<MessageResponse>
}