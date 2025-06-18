package com.sweetspot.api.dto

data class UserLoginResponseDTO(
    val userId: Long,
    val email: String,
    val nickname: String,
    val phoneNumber: String?,
    val isPhoneVerified: Boolean,
    val createdAt: String?,
    val profileImageUrl: String?
)