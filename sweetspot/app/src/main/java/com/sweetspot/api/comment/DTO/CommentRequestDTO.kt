package com.sweetspot.api.comment.DTO

data class CommentRequestDTO(
    val userId: Long,
    val postId: Long,
    val content: String,
)
