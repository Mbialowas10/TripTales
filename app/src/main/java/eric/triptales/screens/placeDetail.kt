package eric.triptales.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import eric.triptales.components.ImageByURL
import eric.triptales.components.PlaceDetailImageSlider
import eric.triptales.components.TopAppBar
import eric.triptales.viewmodel.PlacesViewModel

@Composable
fun PlaceDetailScreen(navController: NavController, viewModel: PlacesViewModel) {
    val placeDetail = viewModel.targetPlace.value
    val reviews = placeDetail?.reviews ?: emptyList()
    val nearbyAttractions = viewModel.nearbyAttractions.value
    placeDetail?.place_id?.let {
        viewModel.checkIfPlaceSaved(it)
    }

    Scaffold(
        topBar = { TopAppBar("Place Detail", "sub", navController) },
        bottomBar = { BottomNavigationBar("Search", navController) }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            item {
                placeDetail?.let {
                    PlaceDetailImageSlider(placeDetail = it)
                }
            }

            item {
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
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = {
                            if (placeDetail != null) {
                                viewModel.toggleSavePlace(placeDetail)
                            }
                        }
                    ) {
                        Text(text = if (viewModel.isSaved.value) "Unsave" else "Save")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Reviews Section
            item {
                Text(
                    text = "Reviews",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(16.dp)
                )
            }
            items(reviews) { review ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp, horizontal = 24.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            ImageByURL(
                                url = review.profile_photo_url,
                                contentDescription = review.author_name,
                                modifier = Modifier
                                    .size(30.dp)
                                    .padding(end = 8.dp)
                            )

                            Text(
                                text = review.author_name,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(
                            text = review.relative_time_description,
                            fontWeight = FontWeight.Thin
                        )
                    }

                    Text(
                        text = "Rating: ${review.rating}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = review.text,
                        style = MaterialTheme.typography.bodySmall
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        thickness = 1.dp,
                        color = Color.Gray
                    )
                }
            }

            // Nearby Attractions Section
            item {
                Text(
                    text = "Nearby Attractions",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(16.dp)
                )
            }
            items(nearbyAttractions) { attraction ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp, horizontal = 16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = attraction.name,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Rating: ${attraction.rating ?: "N/A"}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = attraction.vicinity,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // "See Detail" Button
                        Button(
                            onClick = {
                                navController.navigate("placeDetail")
                                viewModel.getPlaceDetail(attraction.place_id, false)
                            }
                        ) {
                            Text("See Detail")
                        }
                    }
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        thickness = 1.dp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}
