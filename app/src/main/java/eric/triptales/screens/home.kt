package eric.triptales.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import eric.triptales.components.BottomNavigationBar
import eric.triptales.components.TopAppBar
import eric.triptales.firebase.CommunityStory
import eric.triptales.firebase.postStoryToCommunity
import eric.triptales.firebase.getCommunityStories
import eric.triptales.firebase.likeCommunityStory
import eric.triptales.viewmodel.PlacesViewModel

@Composable
fun HomeScreen(navController: NavController, viewModel: PlacesViewModel) {
    var stories by remember { mutableStateOf<List<CommunityStory>>(emptyList()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    // Fetch community stories and handle the result
    fun fetchCommunityStories() {
        getCommunityStories(
            onResult = { stories = it },
            onError = { errorMessage = it }
        )
    }


    Scaffold(
        topBar = { TopAppBar("Home", "main", navController) },
        bottomBar = { BottomNavigationBar("Home", navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Post a story section
            Text(text = "Share a Story", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(8.dp))
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
            Spacer(modifier = Modifier.height(8.dp))

            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    if (title.isNotBlank() && content.isNotBlank()) {
                        isLoading = true
                        val userId = FirebaseAuth.getInstance().currentUser?.uid
                        val username = FirebaseAuth.getInstance().currentUser?.displayName ?: "Anonymous"
                        if (userId != null) {
                            val story = CommunityStory(
                                title = title,
                                content = content,
                                user_id = userId,
                                username = username
                            )
                            postStoryToCommunity(story, onSuccess = {
                                isLoading = false
                                title = ""
                                content = ""
                                fetchCommunityStories() // Refresh stories after posting
                            }, onFailure = {
                                isLoading = false
                                errorMessage = it
                            })
                        } else {
                            errorMessage = "User not authenticated"
                            isLoading = false
                        }
                    } else {
                        errorMessage = "Please enter both title and content."
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Post Story")
            }

            if (errorMessage != null) {
                Text(text = errorMessage!!, color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Display community stories section
            Text(text = "Community Stories", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn {
                items(stories) { story ->
                    CommunityStoryItem(story = story, navController, viewModel)
                }
            }

            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }
        }
    }

    LaunchedEffect(Unit) {
        fetchCommunityStories()
    }
}


@Composable
fun CommunityStoryItem(story: CommunityStory, navController: NavController, viewModel: PlacesViewModel) {
    var likes by remember { mutableStateOf(story.likes) }
    var isLiked by remember { mutableStateOf(false) }
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

    // Check if the current user liked the story
    LaunchedEffect(story.id, currentUserId) {
        if (currentUserId != null) {
            FirebaseFirestore.getInstance()
                .collection("stories_likes")
                .whereEqualTo("storyId", story.id)
                .whereEqualTo("userId", currentUserId)
                .get()
                .addOnSuccessListener { documents ->
                    isLiked = !documents.isEmpty
                }
                .addOnFailureListener {
                    isLiked = false // default to not liked on failure
                }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
    ) {
        Text(text = story.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text(text = story.content, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(vertical = 4.dp))
        Text(text = "Posted by ${story.username}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)

        Spacer(modifier = Modifier.height(8.dp))

        Row {
            Button(onClick = {
                navController.navigate("placeDetail")
                viewModel.getPlaceDetail(story.place_id, false)
            }) {
                Text(text = "See Place")
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = {
                    if (!isLiked && currentUserId != null) {
                        // Add like
                        likeCommunityStory(storyId = story.id, userId = currentUserId, onSuccess = {
                            likes += 1
                            isLiked = true
                        }, onFailure = {
                            // Handle failure if needed
                        })
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = "Like",
                    tint = if (isLiked) Color.Red else Color.Gray
                )
            }
            Text(text = "$likes likes", style = MaterialTheme.typography.bodySmall)
        }
    }
}
