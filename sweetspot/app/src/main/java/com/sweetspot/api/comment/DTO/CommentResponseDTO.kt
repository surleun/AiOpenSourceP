package com.sweetspot.api.comment.DTO

import java.time.LocalDateTime

data class CommentResponseDTO(
    val commentId: Long,
    val userId: Long,
    val postId: Long,
    val content: String,
    val likes: Int,
    val updatedAt: LocalDateTime
)
