package eric.triptales.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import eric.triptales.firebase.CommunityStory
import eric.triptales.firebase.getCommunityStories
import eric.triptales.firebase.postStoryToCommunity

@Composable
fun PostCommunityStoryScreen(onPostSuccess: () -> Unit) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = content,
            onValueChange = { content = it },
            label = { Text("Content") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (title.isNotBlank() && content.isNotBlank()) {
                    val userId = FirebaseAuth.getInstance().currentUser?.uid
                    val username = FirebaseAuth.getInstance().currentUser?.displayName ?: "Anonymous"

                    if (userId != null) {
                        val story = CommunityStory(
                            title = title,
                            content = content,
                            user_id = userId,
                            username = username
                        )
                        postStoryToCommunity(story, onSuccess = onPostSuccess, onFailure = {
                            errorMessage = it
                        })
                    } else {
                        errorMessage = "User not authenticated"
                    }
                } else {
                    errorMessage = "Please fill in both title and content."
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Post Story")
        }

        if (errorMessage != null) {
            Text(text = errorMessage!!, color = MaterialTheme.colorScheme.error)
        }
    }
}

@Composable
fun CommunityScreen() {
    var stories by remember { mutableStateOf<List<CommunityStory>>(emptyList()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        getCommunityStories(
            onResult = { stories = it },
            onError = { errorMessage = it }
        )
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        if (errorMessage != null) {
            Text(text = errorMessage!!, color = MaterialTheme.colorScheme.error)
        }

        LazyColumn {
            items(stories) { story ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Text(text = story.title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Text(text = story.content, style = MaterialTheme.typography.bodyLarge)
                    Text(text = "Posted by ${story.username}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}
