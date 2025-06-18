package com.sweetspot.client.api

import com.sweetspot.api.post.like.PostLikeRequestDto
import com.sweetspot.api.post.DTO.*
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.*

interface MapPostService {

    // 1. 게시글 생성
    @POST("api/posts/create")
    suspend fun createPost(@Body postData: MapPostRequestDTO): Response<MapPostResponseDTO>

    // 2. 게시글 삭제 (postId와 userId를 Path와 Query로 전달)
    @DELETE("api/posts/{postId}")
    suspend fun deletePost(
        @Path("postId") postId: Long,
        @Query("userId") userId: Long
    ): Response<Unit>

    // 3. 게시글 좋아요
    @POST("api/posts/like")
    suspend fun likePost(@Body likeRequest: PostLikeRequestDto): Response<Unit>

    // 4. 게시글 좋아요 취소
    @HTTP(method = "DELETE", path = "api/posts/unlike", hasBody = true)
    suspend fun unlikePost(@Body unlikeRequest: PostLikeRequestDto): Response<Unit>

    // 5. 인기 게시글 조회
    @GET("api/posts/popular")
    suspend fun getPopularPosts(): Response<List<MapPostPopularResponseDTO>>

    // 6. 전체 게시글 리스트 조회
    @GET("api/posts")
    suspend fun getAllPosts(): Response<List<MapPostListResponseDTO>>

    // 7. 특정 게시글 상세 조회
    @GET("api/posts/{postId}")
    suspend fun getPostById(@Path("postId") postId: Long): Response<MapPostDetailResponseDTO>
}
