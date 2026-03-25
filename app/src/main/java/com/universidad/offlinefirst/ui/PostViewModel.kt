package com.universidad.offlinefirst.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.universidad.offlinefirst.data.PostRepository
import com.universidad.offlinefirst.data.local.PostEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PostViewModel(application: Application, private val repo: PostRepository) : AndroidViewModel(application) {

    val posts = repo.observePosts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init {
        viewModelScope.launch {
            repo.refreshIfStale()  // Refresca solo si el cache expiró
        }
    }

    fun toggleFavorite(post: PostEntity) {
        viewModelScope.launch { 
            repo.toggleFavorite(getApplication(), post) 
        }
    }
}
