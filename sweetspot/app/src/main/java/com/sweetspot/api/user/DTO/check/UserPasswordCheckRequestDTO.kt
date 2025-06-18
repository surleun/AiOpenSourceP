package com.sweetspot.api.user.DTO.check

data class UserPasswordCheckRequestDTO(
    val userId: Long,
    val password: String
)
