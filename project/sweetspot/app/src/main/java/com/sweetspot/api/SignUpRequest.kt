package com.sweetspot.api

data class SignUpRequest(
    val email: String,
    val password: String,
    val nickname: String,
    val phoneNumber: String
)
