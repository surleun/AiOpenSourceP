package com.gen.SweetSpot

data class PinItem(
    val id: Long,
    val userId: Long,
    val title: String,
    val content: String?,
    val latitude: Double?,
    val longitude: Double?,
    val createdAt: String?,
    val authorNickname: String,
    val authorProfileImageUrl: String? = null,
    var imageUrl: String? = null,
    var commentCount: Int = 0,
    var likeCount: Int = 0
)