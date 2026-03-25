package com.universidad.offlinefirst

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.universidad.offlinefirst.data.PostRepository
import com.universidad.offlinefirst.data.local.AppDatabase
import com.universidad.offlinefirst.data.local.PostEntity
import com.universidad.offlinefirst.data.remote.RetrofitClient
import com.universidad.offlinefirst.ui.theme.OfflineFirstTheme
import com.universidad.offlinefirst.ui.PostViewModel

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AppDatabase.getDatabase(this)
        val repository = PostRepository(database.postDao(), RetrofitClient.instance)
        
        val viewModelFactory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return PostViewModel(application, repository) as T
            }
        }

        setContent {
            OfflineFirstTheme {
                val viewModel: PostViewModel = viewModel(factory = viewModelFactory)
                val posts by viewModel.posts.collectAsState()

                Scaffold(
                    topBar = {
                        CenterAlignedTopAppBar(title = { Text("Offline-First Posts") })
                    }
                ) { innerPadding ->
                    PostList(
                        posts = posts,
                        onFavoriteClick = { viewModel.toggleFavorite(it) },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun PostList(
    posts: List<PostEntity>,
    onFavoriteClick: (PostEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier.fillMaxSize()) {
        items(posts) { post ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = post.title, style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = post.body, style = MaterialTheme.typography.bodySmall)
                    }
                    IconButton(onClick = { onFavoriteClick(post) }) {
                        Icon(
                            imageVector = if (post.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (post.isFavorite) Color.Red else Color.Gray
                        )
                    }
                }
            }
        }
    }
}
