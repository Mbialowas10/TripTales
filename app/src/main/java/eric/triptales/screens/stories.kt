package eric.triptales.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import eric.triptales.components.BottomNavigationBar
import eric.triptales.components.ImageByURL
import eric.triptales.components.PlaceDetailImageSlider
import eric.triptales.components.TopAppBar
import eric.triptales.viewmodel.PlacesViewModel
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import eric.triptales.components.ImageSliderByBitmap
import eric.triptales.database.StoryEntity
import eric.triptales.firebase.CommunityStory
import eric.triptales.firebase.postStoryToCommunity
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun StoriesScreen(viewModel: PlacesViewModel, navController: NavController){
    val context = LocalContext.current
    val target = viewModel.targetDBPlace.value
    val stories = viewModel.savedStories.value

    var storyTitle by remember { mutableStateOf("") }
    var storyContent by remember { mutableStateOf("") }


    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var savedImages by remember { mutableStateOf<List<Bitmap>>(emptyList()) }

    // Launches an intent to pick an image from the gallery
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),

        // Sets the selected image's URI to selectedImageUri when a result is received
        onResult = { uri -> selectedImageUri = uri }
    )

    // Load images when the target place changes
    LaunchedEffect(target) {
        target?.let {
            savedImages = loadImagesForPlace(context, it.id)

            viewModel.getStoriesForPlace(it.id)
        }
    }

    Scaffold(
        topBar = { TopAppBar("Stories", "sub", navController) },
        bottomBar = { BottomNavigationBar("Search", navController) }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(16.dp))

                    // Button to select an image
                    Button(onClick = { imagePickerLauncher.launch("image/*") }) {
                        Text("Select Image")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Converts the selected URI to a Bitmap
                    selectedImageUri?.let { uri ->
                        // Checks Android version for compatibility
                        val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            // For Android P (API 28) and above, uses ImageDecoder
                            ImageDecoder.decodeBitmap(
                                ImageDecoder.createSource(context.contentResolver, uri)
                            )
                        } else {
                            // Using MediaStore below API 28
                            @Suppress("DEPRECATION")
                            MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                        }

                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = "Selected Image",
                            modifier = Modifier.size(150.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Save the image locally with place ID when "Save Image" is clicked
                        Button(
                            onClick = {
                                target?.let {
                                    if (saveImageLocally(context, bitmap, it.id)) {
                                        savedImages = loadImagesForPlace(context, it.id) // Reload images
                                        selectedImageUri = null
                                    }
                                }
                            }
                        ) {
                            Text("Save Image")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Display images: single image or slider based on the number of images
                    if (savedImages.size == 1) {
                        // Display a single image if there's only one
                        Image(
                            bitmap = savedImages[0].asImageBitmap(),
                            contentDescription = "Saved Image",
                            modifier = Modifier.size(150.dp)
                        )
                    } else if (savedImages.size > 1) {
                        // Display an image slider if there are multiple images
                        ImageSliderByBitmap(images = savedImages)
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))

                // Place Name Section
                Text(
                    text = target?.name ?: "No Name Available",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(16.dp)
                )

                // Place Address Section
                Text(
                    text = target?.address ?: "No Address Available",
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Place Rating and Phone
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Rating: ${target?.rating ?: "N/A"}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Phone: ${target?.formatted_phone_number ?: "N/A"}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Place Website Section
                Text(
                    text = "Website: ${target?.website ?: "N/A"}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Unsaved Button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = {
                            if(target != null){
                                viewModel.deletePlaceFromDB(context ,target.id)
                            }
                        }
                    ) {
                        Text(text = "Unsaved")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                Text(
                    text = "Stories",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(16.dp)
                )
            }

            items(stories) { story ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Title with larger font size
                    Text(
                        text = story.title,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge
                    )

                    // Content with default font size
                    Text(
                        text = story.content,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )

                    // Display the formatted creation date
                    val dateFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                    val formattedDate = dateFormatter.format(Date(story.created_at))
                    Text(
                        text = "Created: $formattedDate",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Row for the Share and Delete buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Share Button with default color
                        Button(onClick = {
                            val userId = FirebaseAuth.getInstance().currentUser?.uid
                            val username = FirebaseAuth.getInstance().currentUser?.displayName ?: "Anonymous"
                            if (userId != null) {
                                val storyEntry = CommunityStory(
                                    title = story.title,
                                    content = story.content,
                                    user_id = userId,
                                    username = username,
                                    place_id = target?.id ?: "",
                                    place_photos = target?.photos,
                                    place_address = target?.address,
                                    place_name = target?.name
                                )
                                postStoryToCommunity(storyEntry, onSuccess = {}, onFailure = {})
                            }
                        }) {
                            Text("Share")
                        }

                        // Delete Button in red
                        Button(
                            onClick = {viewModel.deleteStory(story.story_id)},
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Red,
                                contentColor = Color.White
                            )
                        ) {
                            Text("Delete")
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = Color.Gray)
            }

            // Add new story section
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Add New Story",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(16.dp)
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    TextField(
                        value = storyTitle,
                        onValueChange = { storyTitle = it },
                        label = { Text("Story Title") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    TextField(
                        value = storyContent,
                        onValueChange = { storyContent = it },
                        label = { Text("Story Content") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            val newStory = StoryEntity(
                                title = storyTitle,
                                content = storyContent,
                                created_at = System.currentTimeMillis(),
                                place_id = target?.id ?: ""
                            )
                            viewModel.addStory(newStory)
                            storyTitle = ""
                            storyContent = ""
                        }
                    ) {
                        Text("Post Story")
                    }
                }
            }
        }
    }
}

// The function is responsible for storing the Bitmap in the app’s private internal storage.
// The filename includes both placeId and a timestamp to ensure each image has
// a unique name, allowing multiple images per place.
fun saveImageLocally(context: Context, bitmap: Bitmap, placeId: String): Boolean {
    // Define unique filename
    val fileName = "saved_image_${placeId}_${System.currentTimeMillis()}.jpg"
    return try {
        // Create file object
        val file = File(context.filesDir, fileName)

        // Opens an output stream to write to the file
        val outputStream: OutputStream = FileOutputStream(file)

        // Compresses and writes the bitmap to the file as JPEG
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)

        // "Push & commit" written data
        outputStream.flush()
        outputStream.close()

        // Return true after success
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

// The function loads all images associated with a specific
// placeId from internal storage, returning them as a list of Bitmap objects.
fun loadImagesForPlace(context: Context, placeId: String): List<Bitmap> {
    // Gets the internal storage directory
    val filesDir = context.filesDir

    // Loop and filter files by Place id prefix
    val files = filesDir.listFiles { file ->
        file.name.startsWith("saved_image_$placeId") && file.name.endsWith(".jpg")
    } ?: return emptyList()

    // Return decoded file (a Bitmap Object)
    // mapNotNull will ignore nulls
    return files.mapNotNull { file ->
        BitmapFactory.decodeFile(file.path)
    }
}

// The function deletes all images associated
// with a given placeId by filtering files in the internal storage
// and deleting them individually.
fun deleteImagesForPlace(context: Context, placeId: String): Boolean {
    // Gets the internal storage directory
    val filesDir = context.filesDir

    // Loop and filter files by Place id prefix
    val files = filesDir.listFiles { file ->
        file.name.startsWith("saved_image_$placeId") && file.name.endsWith(".jpg")
    } ?: return false

    var success = true

    // Loop and delete file
    for (file in files) {
        if (!file.delete()){
            success = false
        }
    }
    return success
}
