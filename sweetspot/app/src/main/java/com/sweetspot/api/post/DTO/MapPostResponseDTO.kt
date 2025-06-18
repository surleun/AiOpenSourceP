package com.sweetspot.api.post.DTO

import com.sweetspot.api.LocalDateTimeAdapter

data class MapPostResponseDTO(
    val postId: Long,
    val userId: Long,
    val pinId: Long,
    val title: String,
    val content: String,
    val likes: Int,
    val updatedAt: LocalDateTimeAdapter
)
