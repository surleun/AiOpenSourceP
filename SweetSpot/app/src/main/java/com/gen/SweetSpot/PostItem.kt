package com.gen.SweetSpot

data class PostItem(
    val id: Long,
    val userId: Long,
    var title: String,
    var content: String?,
    val createdAt: String?,
    var views: Int = 0,
    var authorNickname: String,
    val authorProfileImageUrl: String? = null,
    var imageUrl: String? = null,
    var commentCount: Int = 0,
    var likeCount: Int = 0
)