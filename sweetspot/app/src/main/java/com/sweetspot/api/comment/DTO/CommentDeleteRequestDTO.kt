package com.sweetspot.api.comment.DTO

data class CommentDeleteRequestDTO (
    val commentId: Long,
    val userId: Long
)