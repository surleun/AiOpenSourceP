package com.sweetspot.api.user.DTO

import java.time.LocalDateTime

data class UserInfoResponseDTO(
    val email: String,
    val phoneNumber: String?,
    val nickname: String,
    val isPhoneVerified: Boolean,
    val profileImageUrl: String?,
    val createdAt: LocalDateTime,
    //val posts: List<UserPostSummaryDTO>,
    //val comments: List<UserCommentDTO>,
    //val likedPosts: List<UserLikedPostDTO>,
    //val likedComments: List<UserLikedCommentDTO>
)