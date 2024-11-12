package eric.triptales.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = place.name, style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Button for "Unsaved" with gray background
                Button(
                    onClick = { viewModel.deletePlaceFromDB(context, place.id) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                    modifier = Modifier.height(36.dp).padding(horizontal = 4.dp)
                ) {
                    Text("Unsaved")
                }

                // Button for "View Stories"
                Button(
                    onClick = {
                        viewModel.setTargetDBPlace(place.id)
                        navController.navigate("stories")
                    },
                    modifier = Modifier.height(36.dp).padding(horizontal = 4.dp)
                ) {
                    Text("View Stories")
                }

                // Button for "See Details"
                Button(
                    onClick = {
                        navController.navigate("placeDetail")
                        viewModel.getPlaceDetail(place.id, false)
                    },
                    modifier = Modifier.height(36.dp)
                ) {
                    Text("Details")
                }
            }
        }
    }
}
