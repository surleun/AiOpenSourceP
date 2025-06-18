package com.sweetspot.api

import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

data class RegisterRequest(
    val userId: String,
    val password: String,
    val nickname: String,
    val phone: String
)
data class RegisterResponse(val success: Boolean, val message: String)

data class CheckRequest(val value: String)
data class CheckResponse(val available: Boolean, val message: String)

data class LoginRequest(val userId: String, val password: String)
data class LoginResponse(val success: Boolean, val token: String?, val message: String)

data class ApiResponse(val success: Boolean, val message: String)

interface UserApi {
    @POST("/api/users/register")
    fun register(@Body req: RegisterRequest): Call<RegisterResponse>

    // 이메일 제외 → 전화번호 중복 확인만
    @POST("/api/users/register/check/phone")
    fun checkPhone(@Body req: CheckRequest): Call<CheckResponse>

    @POST("/api/users/login")
    fun login(@Body req: LoginRequest): Call<LoginResponse>

    @POST("/api/users/logout")
    fun logout(): Call<ApiResponse>

    @Multipart
    @POST("/api/users/update/image")
    fun uploadProfileImage(@Part file: MultipartBody.Part): Call<ApiResponse>

    @POST("/api/users/image/delete")
    fun deleteProfileImage(): Call<ApiResponse>

    @PUT("/api/users/update/nickname")
    fun updateNickname(@Body map: Map<String, String>): Call<ApiResponse>

    @POST("/api/users/check/password")
    fun checkPassword(@Body map: Map<String, String>): Call<ApiResponse>

    @POST("/api/users/update/password")
    fun updatePassword(@Body map: Map<String, String>): Call<ApiResponse>


}