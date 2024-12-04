package eric.triptales.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import eric.triptales.components.BottomNavigationBar
import eric.triptales.components.TopAppBar
import eric.triptales.database.StoryEntity
import eric.triptales.firebase.entity.CommunityStory
import eric.triptales.firebase.entity.SavedPlaceEntity
import eric.triptales.firebase.functions.fetchSavedStories
import eric.triptales.firebase.functions.postStoryToCommunity
import eric.triptales.firebase.functions.getCommunityStories
import eric.triptales.firebase.functions.likeCommunityStory
import eric.triptales.viewmodel.PlacesViewModel

@Composable
fun HomeScreen(navController: NavController, viewModel: PlacesViewModel) {
    var stories by remember { mutableStateOf<List<CommunityStory>>(emptyList()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var isPosting by remember { mutableStateOf(false) }

    val userId = FirebaseAuth.getInstance().currentUser?.uid
    var savedPlaces by remember { mutableStateOf<List<SavedPlaceEntity>>(emptyList()) }
    var isDropdownExpanded by remember { mutableStateOf(false) }
    var selectedPlace by remember { mutableStateOf<SavedPlaceEntity?>(null) }


    // Fetch community stories and handle the result
    fun fetchCommunityStories() {
        getCommunityStories(
            onResult = { stories = it },
            onError = { errorMessage = it }
        )
    }

    LaunchedEffect(userId){
        userId?.let {
            fetchSavedStories(
                userId,
                onSuccess = { places -> savedPlaces = places },
                onFailure = {})
        }
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
            if(isPosting){
                // Post a story section
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween, // Space-between behavior
                    verticalAlignment = Alignment.CenterVertically // Align items vertically center
                ) {
                    Text(
                        text = "Share a Story",
                        style = MaterialTheme.typography.headlineMedium
                    )
                    IconButton(onClick = { isPosting = false }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Arrow Down"
                        )
                    }
                }
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

                Box(modifier = Modifier.fillMaxWidth()) {
                    IconButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(shape = RectangleShape)
                            .padding(vertical = 12.dp),
                        onClick = { isDropdownExpanded = !isDropdownExpanded }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.LightGray),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Select place")
                            Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "Select Place")
                        }
                    }

                    // Dropdown for selecting a saved place
                    DropdownMenu(
                        expanded = isDropdownExpanded,
                        onDismissRequest = { isDropdownExpanded = false },
                        modifier = Modifier.fillMaxWidth(),
                        offset = DpOffset(x = 0.dp, y = 8.dp) // Offset dropdown to appear below the button
                    ) {
                        savedPlaces.forEach { place ->
                            DropdownMenuItem(
                                text = { Text(place.name) },
                                onClick = {
                                    Log.e("places", place.toString())
                                    selectedPlace = place
                                    isDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                // Show selected place
                if (selectedPlace != null) {
                    Text(text = "Selected Place: ${selectedPlace!!.name}")
                }


                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        if (title.isNotBlank() && content.isNotBlank() && selectedPlace != null) {
                            isLoading = true
                            val userId = FirebaseAuth.getInstance().currentUser?.uid
                            val username = FirebaseAuth.getInstance().currentUser?.displayName ?: "Anonymous"
                            if (userId != null) {
                                val story = CommunityStory(
                                    title = title,
                                    content = content,
                                    user_id = userId,
                                    username = username,
                                    place_id = selectedPlace!!.placeId,
                                    place_photos = selectedPlace!!.photos,
                                    place_name = selectedPlace!!.name,
                                    place_address = selectedPlace!!.address
                                )
                                postStoryToCommunity(story, onSuccess = {
                                    val localStory = StoryEntity(
                                        title = title,
                                        content = content,
                                        created_at = System.currentTimeMillis(),
                                        place_id = selectedPlace!!.placeId,
                                    )
                                    viewModel.addStory(localStory)
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
                            isPosting = false
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
            } else {
                Column (
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Button(onClick = {
                        isPosting = true
                    }) {
                        Text(text = "Wanna share new story?")
                    }
                }
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
fun CommunityStoryItem(
    story: CommunityStory,
    navController: NavController,
    viewModel: PlacesViewModel
) {
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
            .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
            .padding(vertical = 8.dp)
            .background(MaterialTheme.colorScheme.surface)
            .padding(8.dp)
    ) {
        // Title
        Text(
            text = story.title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        // Posted by
        Text(
            text = "Posted by ${story.username}",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray,
            modifier = Modifier.padding(vertical = 4.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Content
        Text(
            text = story.content,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(vertical = 4.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Place Box with Image and See Place Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                .clickable {
                    viewModel.getPlaceDetail(story.place_id, true)
                    navController.navigate("placeDetail")
                }
                .padding(8.dp)
        ) {
            Column {
                // Display the place image if available
                story.place_photos?.firstOrNull()?.let { photo ->
                    Log.e("Image", photo)
                    val photoUrl = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=${photo}&key=AIzaSyBQtniS0NCgJc5D5g_t_ke42u5_ttYn4Rw"

                    Image(
                        painter = rememberAsyncImagePainter(photoUrl),
                        contentDescription = "Place Image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = story.place_name ?: "",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Black,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = story.place_address ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Black,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // See Place text
                Text(
                    text = "See Place",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.align(Alignment.Start)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Like Button Row
        Row(horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically) {
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

    Spacer(modifier = Modifier.height(16.dp))
}
