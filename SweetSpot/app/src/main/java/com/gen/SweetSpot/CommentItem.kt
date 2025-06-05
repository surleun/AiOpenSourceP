package com.gen.SweetSpot

data class CommentItem(
    val id: Long,
    val postId: Long,
    val postType: String,
    val userId: Long,
    var content: String,
    val createdAt: String,
    var authorNickname: String,
    val authorProfileImageUrl: String? = null,
    var likeCount: Int = 0
)