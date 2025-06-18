package com.sweetspot.client.api

import com.sweetspot.api.user.DTO.UserIdRequestDTO
import com.sweetspot.api.user.DTO.UserInfoResponseDTO
import com.sweetspot.api.user.DTO.update.UserImageUpdateDTO
import com.sweetspot.api.user.DTO.update.UserNicknameUpdateDTO
import com.sweetspot.client.model.CheckEmailRequest
import com.sweetspot.client.model.CheckEmailResponse
import com.sweetspot.client.model.RegisterResponse
import com.sweetspot.client.model.LoginRequest
import com.sweetspot.client.model.LoginResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.PUT
import retrofit2.Response

interface ApiService {

    @Headers("Content-Type: application/json")
    @POST("/api/users/register/check/email")
    fun checkEmail(@Body request: CheckEmailRequest): Call<CheckEmailResponse>

    // Multipart로 회원가입
    @Multipart
    @POST("/api/users/register")
    fun registerWithImage(
        @Part("data") data: RequestBody,
        @Part profileImage: MultipartBody.Part?  // null 허용
    ): Call<RegisterResponse>

    @Headers("Content-Type: application/json")
    @POST("/api/users/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @Headers("Content-Type: application/json")
    @POST("/api/users/info")
    suspend fun getUserInfo(
        @Body request: UserIdRequestDTO
    ): Response<UserInfoResponseDTO>

}
