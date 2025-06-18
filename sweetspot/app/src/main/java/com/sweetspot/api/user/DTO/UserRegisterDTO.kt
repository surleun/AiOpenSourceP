package com.sweetspot.api.user.DTO

import java.time.LocalDateTime

data class UserRegisterDTO(
    val userId: Long,
    val email: String,
    val password: String,
    val nickname: String,
    val phoneNumber: String,
    val isPhoneVerified: Boolean,
    val createdAt: LocalDateTime,
    val profileImageUrl: String,
    val profileImageName: String,
)