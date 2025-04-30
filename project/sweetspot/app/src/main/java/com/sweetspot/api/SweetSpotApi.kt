package com.sweetspot.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Body

data class MessageResponse(val message: String)

interface SweetSpotApi {
    @GET("/") // 서버 기본 주소에 GET 요청
    fun getHome(): Call<MessageResponse>

    @POST("/api/users/register") // 회원가입
    fun signUp(@Body signUpRequest: SignUpRequest): Call<Void>
}