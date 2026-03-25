package com.universidad.offlinefirst.data

import com.universidad.offlinefirst.data.local.PostDao
import com.universidad.offlinefirst.data.local.PostEntity
import com.universidad.offlinefirst.data.remote.PostApiService
import com.universidad.offlinefirst.data.remote.toEntity
import kotlinx.coroutines.flow.Flow

class PostRepository(private val dao: PostDao, private val api: PostApiService) {

    companion object {
        private const val TTL_MS = 5 * 60 * 1000L  // 5 minutos
    }

    // Fuente única de verdad: la UI siempre observa Room
    fun observePosts(): Flow<List<PostEntity>> = dao.observeAll()

    // Lógica de refresco: solo llama a la red si el cache expiró
    suspend fun refreshIfStale() {
        val oldest = dao.getOldestCacheTimestamp() ?: 0L
        val isStale = (System.currentTimeMillis() - oldest) > TTL_MS
        if (isStale) {
            try {
                val remote = api.getPosts()
                dao.upsertAll(remote.map { it.toEntity() })
            } catch (e: Exception) {
                // Sin conexión: Room ya tiene datos, no se hace nada
            }
        }
    }

    suspend fun toggleFavorite(post: PostEntity) {
        dao.toggleFavorite(post.id, !post.isFavorite)
        // WorkManager encola la sincronización (ver Paso 5)
    }
}
