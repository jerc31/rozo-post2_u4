package com.universidad.offlinefirst.data

import android.util.Log
import com.universidad.offlinefirst.data.local.PostDao
import com.universidad.offlinefirst.data.local.PostEntity
import com.universidad.offlinefirst.data.remote.PostApiService
import com.universidad.offlinefirst.data.remote.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach

class PostRepository(
    private val postDao: PostDao,
    private val apiService: PostApiService
) {
    companion object {
        private const val TTL_MILLIS = 5 * 60 * 1000 // 5 minutes
    }

    fun getPosts(): Flow<List<PostEntity>> {
        return postDao.observeAll().onEach { localPosts ->
            if (shouldUpdateCache()) {
                refreshPosts()
            }
        }
    }

    private suspend fun shouldUpdateCache(): Boolean {
        val oldest = postDao.getOldestCacheTimestamp() ?: return true
        return (System.currentTimeMillis() - oldest) > TTL_MILLIS
    }

    suspend fun refreshPosts() {
        try {
            val remotePosts = apiService.getPosts()
            Log.d("PostRepository", "Fetched ${remotePosts.size} posts from network")
            postDao.upsertAll(remotePosts.map { it.toEntity() })
        } catch (e: Exception) {
            Log.e("PostRepository", "Error refreshing posts", e)
        }
    }

    suspend fun toggleFavorite(id: Int, isFavorite: Boolean) {
        postDao.toggleFavorite(id, isFavorite)
        // WorkManager will handle the sync later
    }
}
