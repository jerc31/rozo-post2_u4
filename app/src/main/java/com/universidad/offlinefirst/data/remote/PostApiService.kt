package com.universidad.offlinefirst.data.remote

import com.universidad.offlinefirst.data.local.PostEntity
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path

interface PostApiService {
    @GET("posts")
    suspend fun getPosts(): List<PostDto>

    @PATCH("posts/{id}")
    suspend fun updateFavorite(
        @Path("id") id: Int,
        @Body body: Map<String, Boolean>
    ): PostDto
}

data class PostDto(
    val id: Int,
    val title: String,
    val body: String,
    val userId: Int
)

fun PostDto.toEntity() = PostEntity(
    id = id,
    title = title,
    body = body,
    userId = userId
)
