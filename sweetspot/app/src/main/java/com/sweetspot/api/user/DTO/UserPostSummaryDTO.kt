package com.sweetspot.client.model

import java.time.LocalDateTime

data class UserPostSummaryDTO(
    val postId: Long,
    val title: String,
    val updatedAt: LocalDateTime,
    val likes: Int
)