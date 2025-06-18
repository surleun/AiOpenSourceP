package com.sweetspot.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import com.sweetspot.api.LoginRequest
import com.sweetspot.api.LoginResponse
// 회원가입 요청 바디
data class SignUpRequest(
    val userId: String,
    val nickname: String,
    val password: String,
    val phone: String
)
// 회원가입 응답 바디
data class SignUpResponse(
    val success: Boolean,
    val message: String
)

interface AuthApi {
    @POST("auth/login")
    fun login(@Body req: LoginRequest): Call<LoginResponse>

    @POST("auth/signup")
    fun signUp(@Body req: SignUpRequest): Call<SignUpResponse>
}
