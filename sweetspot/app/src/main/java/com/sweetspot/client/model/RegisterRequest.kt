package com.sweetspot.client.model

data class RegisterRequest(
    val email: String,
    val password: String,
    val nickname: String
)