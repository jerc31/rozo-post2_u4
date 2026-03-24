package com.universidad.offlinefirst

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.universidad.offlinefirst.data.remote.RetrofitClient
import com.universidad.offlinefirst.ui.theme.OfflineFirstTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Manual test for Checkpoint 1
        lifecycleScope.launch {
            try {
                val posts = RetrofitClient.instance.getPosts()
                Log.d("CHECKPOINT_1", "Received ${posts.size} posts")
                posts.take(5).forEach { post ->
                    Log.d("CHECKPOINT_1", "Post: ${post.title}")
                }
            } catch (e: Exception) {
                Log.e("CHECKPOINT_1", "Error fetching posts", e)
            }
        }

        setContent {
            OfflineFirstTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Offline-First App",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}
