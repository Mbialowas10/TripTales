package eric.triptales.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import eric.triptales.api.PlaceResult
import eric.triptales.viewmodel.PlacesViewModel

/**
 * A composable function that displays a card for a place with options for navigation and actions.
 *
 * The `PlaceCard` function dynamically adjusts its content and buttons based on the provided `type`.
 * It supports displaying details of a place and navigating to relevant screens based on user interaction.
 *
 * @param place The [PlaceResult] representing the place to display.
 * @param type A [String] indicating the card type ("nearby" or "autocomplete") and its behavior.
 * @param viewModel The [PlacesViewModel] for interacting with the place data.
 * @param navController The [NavController] used for navigation actions.
 */
@Composable
fun PlaceCard(
    place: PlaceResult,
    type: String,
    viewModel: PlacesViewModel,
    navController: NavController
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp)
        ) {
            // Header row with place name and icon
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Place,
                    contentDescription = "Place Icon",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = place.name,
                    fontSize = 20.sp,
                    color = Color.Black,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Show place vicinity for "nearby" type
            if (type == "nearby") {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = place.vicinity,
                    fontSize = 12.sp,
                    color = Color.LightGray,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Display action buttons based on card type
            when (type) {
                "autocomplete" -> {
                    Button(
                        onClick = {
                            navController.navigate("nearbySearch")
                            viewModel.getPlaceDetail(place.place_id, true)
                        },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Search Nearby")
                    }
                }
                "nearby" -> {
                    Button(
                        onClick = {
                            navController.navigate("placeDetail")
                            viewModel.getPlaceDetail(place.place_id, true)
                        },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("See Detail")
                    }
                }
            }
        }
    }
}
