package com.sweetspot.client.model

data class LoginResponse(
    val userId: Long,
    val email: String,
    val nickname: String,
    val profileImageUrl: String
)