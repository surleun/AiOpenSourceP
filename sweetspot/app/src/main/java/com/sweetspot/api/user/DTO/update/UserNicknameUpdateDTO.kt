package com.sweetspot.api.user.DTO.update

data class UserNicknameUpdateDTO(
    val userId: Long,
    val newNickname: String
)
