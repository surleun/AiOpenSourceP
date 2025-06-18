package com.sweetspot.api.post.like

data class PostLikeRequestDto(
    val userId: Long,
    val postId: Long,
)
