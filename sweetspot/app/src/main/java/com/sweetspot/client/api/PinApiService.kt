package com.sweetspot.client.api

import com.sweetspot.api.user.DTO.UserIdRequestDTO
import com.sweetspot.api.user.DTO.UserInfoResponseDTO
import com.sweetspot.client.model.CheckEmailRequest
import com.sweetspot.client.model.CheckEmailResponse
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

interface PinApiService {

    @Headers("Content-Type: application/json")
    @POST("/api/users/info")
    suspend fun getUserInfo(
        @Body request: UserIdRequestDTO
    ): Response<UserInfoResponseDTO>

}
