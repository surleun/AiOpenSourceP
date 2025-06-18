package com.sweetspot.api.user.DTO.check

data class UserPasswordCheckResponseDTO(
    val match: Boolean,
    val message: String
)
