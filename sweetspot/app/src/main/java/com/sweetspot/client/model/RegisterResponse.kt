package com.sweetspot.client.model

data class RegisterResponse(
    val userId: Long,
    val email: String,
    val nickname: String
)