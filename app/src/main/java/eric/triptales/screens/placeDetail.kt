package eric.triptales.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import eric.triptales.api.PlaceDetailResult
import eric.triptales.components.BottomNavigationBar
import eric.triptales.components.TopAppBar
import eric.triptales.viewmodel.PlacesViewModel

@Composable
fun PlaceDetailScreen(navController: NavController, viewModel: PlacesViewModel) {
    val API_KEY = "AIzaSyBQtniS0NCgJc5D5g_t_ke42u5_ttYn4Rw"
    val placeDetail = viewModel.targetPlace.value

    placeDetail?.place_id?.let {
        viewModel.checkIfPlaceSaved(it)
    }

    Scaffold(
        topBar = { TopAppBar("Place Detail", "sub", navController) },
        bottomBar = { BottomNavigationBar("Search", navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if(!viewModel.isFetchDetail.value){
                // Section to load the place image
                val photoReference = placeDetail?.photos?.get(0)?.photo_reference
                val imageUrl = if (photoReference != null) {
                    "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=$photoReference&key=$API_KEY"
                } else {
                    null // Fallback if no photo available
                }

                imageUrl?.let {
                    Image(
                        painter = rememberAsyncImagePainter(it),
                        contentDescription = "Place Image",
                        modifier = Modifier
                            .height(200.dp)
                            .fillMaxWidth(),
                        contentScale = ContentScale.Crop
                    )
                } ?: Box(
                    modifier = Modifier
                        .height(200.dp)
                        .fillMaxWidth()
                        .background(Color.Gray),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "No Image Available")
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Place Name Section
                Text(
                    text = placeDetail?.name ?: "No Name Available",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(16.dp)
                )

                // Place Address Section
                Text(
                    text = placeDetail?.formatted_address ?: "No Address Available",
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
                        text = "Rating: ${placeDetail?.rating ?: "N/A"}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Phone: ${placeDetail?.formatted_phone_number ?: "N/A"}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Place Website Section
                Text(
                    text = "Website: ${placeDetail?.website ?: "N/A"}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Save/Unsave Button
                Button(
                    onClick = {
                        if(placeDetail != null){
                            viewModel.toggleSavePlace(placeDetail)
                        }
                    },
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .align(Alignment.End)
                ) {
                    Text(text = if (viewModel.isSaved.value) "Unsave" else "Save")
                }

            }
        }
    }
}
