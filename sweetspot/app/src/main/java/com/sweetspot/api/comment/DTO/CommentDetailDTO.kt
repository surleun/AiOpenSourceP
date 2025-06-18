package com.sweetspot.api.comment.DTO

import java.time.LocalDateTime

data class CommentDetailDTO (
    val commentId: Long,
    val userId: Long,
    val nickname: String,
    val content: String,
    val updatedAt: LocalDateTime,
    val likes: Int
)