package com.sweetspot.api.comment.like

data class CommentLikeRequestDto(
    val userId: Long,
    val commentId: Long
)
