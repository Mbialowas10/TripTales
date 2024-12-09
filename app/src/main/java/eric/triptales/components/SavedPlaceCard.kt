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

/**
 * A composable function that displays a card for a saved place.
 *
 * The `SavedPlaceCard` includes an image slider (if photos are available), place details,
 * and action buttons for interacting with the place (e.g., unsaving, viewing stories, or navigating to details).
 *
 * @param place The [PlaceEntity] containing the saved place details.
 * @param viewModel The [PlacesViewModel] used to manage saved places and interact with the database.
 * @param navController The [NavController] used for navigation actions.
 */
@Composable
fun SavedPlaceCard(
    place: PlaceEntity,
    viewModel: PlacesViewModel,
    navController: NavController
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp), // Adds vertical spacing between cards
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp) // Adds a shadow to the card
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Display an image slider or a fallback message if no photos are available
            if (!place.photos.isNullOrEmpty()) {
                /**
                 * Displays a slider with images of the place using [PlaceEntityImageSlider].
                 */
                PlaceEntityImageSlider(place)
            } else {
                /**
                 * Displays a placeholder when no images are available.
                 */
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

            // Display the name of the place
            Text(
                text = place.name,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            // Display the address of the place, if available
            if (place.address.isNotEmpty()) {
                Text(
                    text = place.address,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Display action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                /**
                 * Button to remove the place from the saved list.
                 */
                Button(
                    onClick = { viewModel.deletePlaceFromDB(context, place.id) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text("Unsaved")
                }

                /**
                 * Button to navigate to the "View Stories" screen for the place.
                 */
                Button(
                    onClick = {
                        viewModel.setTargetDBPlace(place.id)
                        navController.navigate("stories")
                    },
                    modifier = Modifier.height(36.dp)
                ) {
                    Text("View Stories")
                }

                /**
                 * Button to navigate to the details screen for the place.
                 */
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
