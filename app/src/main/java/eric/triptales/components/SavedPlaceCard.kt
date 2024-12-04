package eric.triptales.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import eric.triptales.database.PlaceEntity
import eric.triptales.viewmodel.PlacesViewModel

@Composable
fun SavedPlaceCard(place: PlaceEntity, viewModel: PlacesViewModel, navController: NavController) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Display the image slider
            if (!place.photos.isNullOrEmpty()) {
                PlaceEntityImageSlider(place)
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(Color.Gray),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "No Image Available", color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Display place name and address
            Text(
                text = place.name,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            if (place.address.isNotEmpty()) {
                Text(
                    text = place.address,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // "Unsaved" Button
                Button(
                    onClick = { viewModel.deletePlaceFromDB(context, place.id) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text("Unsaved")
                }

                // "View Stories" Button
                Button(
                    onClick = {
                        viewModel.setTargetDBPlace(place.id)
                        navController.navigate("stories")
                    },
                    modifier = Modifier.height(36.dp)
                ) {
                    Text("View Stories")
                }

                // "Details" Button
                Button(
                    onClick = {
                        navController.navigate("placeDetail")
                        viewModel.getPlaceDetail(place.id, true)
                    },
                    modifier = Modifier.height(36.dp)
                ) {
                    Text("Details")
                }
            }
        }
    }
}

