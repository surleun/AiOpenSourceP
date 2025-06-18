package com.sweetspot.api.post.DTO

data class MapPostRequestDTO(
    val userId: Long,
    val pinId: Long,
    val title: String,
    val content: String
)
