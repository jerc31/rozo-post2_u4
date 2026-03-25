package com.universidad.offlinefirst.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.universidad.offlinefirst.data.local.AppDatabase
import com.universidad.offlinefirst.data.remote.RetrofitClient
import java.io.IOException

class SyncFavoritesWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {

    override suspend fun doWork(): Result {
        val db = AppDatabase.getDatabase(applicationContext)
        val api = RetrofitClient.instance
        val pending = db.postDao().getPendingSync()

        return try {
            pending.forEach { post ->
                api.updateFavorite(post.id, mapOf("favorite" to post.isFavorite))
                db.postDao().markSynced(post.id)
            }
            Result.success()
        } catch (e: IOException) {
            if (runAttemptCount < 3) Result.retry() else Result.failure()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}
