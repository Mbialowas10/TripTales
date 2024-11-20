package eric.triptales.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import eric.triptales.components.BottomNavigationBar
import eric.triptales.firebase.SavedPlaceEntity
import eric.triptales.firebase.fetchSavedStories
import eric.triptales.viewmodel.DirectionsViewModel
import eric.triptales.viewmodel.PlacesViewModel

@Composable
fun PlacePickingScreen(
    directionViewModel: DirectionsViewModel,
    navController: NavController
) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid

    // State for selected places, saved places, and waypoints
    val selectedPlaces = remember { mutableStateListOf<SavedPlaceEntity>() }
    var savedPlaces by remember { mutableStateOf<List<SavedPlaceEntity>>(emptyList()) }
    val waypoints = remember { mutableStateListOf<SavedPlaceEntity>() }

    // Calculate remaining places dynamically
    val remainingPlaces = savedPlaces.filter { it !in selectedPlaces && it !in waypoints }

    // Fetch saved places when the screen is launched
    LaunchedEffect(userId) {
        userId?.let {
            fetchSavedStories(
                userId = it,
                onSuccess = { places -> savedPlaces = places },
                onFailure = { /* Handle failure */ }
            )
        }
    }

    Scaffold(
        topBar = { eric.triptales.components.TopAppBar("Planning a Trip", "sub", navController) },
        bottomBar = { BottomNavigationBar("Plan", navController) },
    ) {
        paddingValues ->
        Column(modifier = Modifier.fillMaxSize()) {
            // LazyColumn for Origin, Destination, and Waypoints
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(paddingValues)
                    .padding(8.dp)
            ) {
                // PlaceItem for Origin
                item {
                    PlaceItem(
                        label = "Origin",
                        places = remainingPlaces,
                        onPlaceSelected = { place ->
                            if (selectedPlaces.size >= 1) selectedPlaces[0] = place
                            else selectedPlaces.add(place)
                        }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // PlaceItem for Destination
                item {
                    PlaceItem(
                        label = "Destination",
                        places = remainingPlaces,
                        onPlaceSelected = { place ->
                            if (selectedPlaces.size >= 2) selectedPlaces[1] = place
                            else selectedPlaces.add(place)
                        }
                    )
                }

                // Dynamically added PlaceItems for Waypoints
                items(waypoints.size) { index ->
                    PlaceItem(
                        label = "Waypoint ${index + 1}",
                        places = remainingPlaces,
                        onPlaceSelected = { place ->
                            waypoints[index] = place
                        }
                    )
                }
            }

            // Button to Add a New Waypoint
            Button(
                onClick = { waypoints.add(SavedPlaceEntity(placeId = "", name = "Select Place")) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("Add Waypoint")
            }

            // "Done" Button
            Button(
                onClick = {
                    if (selectedPlaces.size >= 2) {
                        val origin = selectedPlaces[0].placeId
                        val destination = selectedPlaces[1].placeId
                        val waypointIds = waypoints.map { it.placeId }

                        // Fetch routes from DirectionsViewModel
                        directionViewModel.fetchRoutes(origin, destination, waypointIds)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                enabled = selectedPlaces.size >= 2 // Enable button if at least 2 places are selected
            ) {
                Text("Done")
            }
        }
    }


}

@Composable
fun PlaceItem(
    label: String,
    places: List<SavedPlaceEntity>,
    onPlaceSelected: (SavedPlaceEntity) -> Unit
) {
    var isDropdownExpanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.bodyMedium
        )

        Box(modifier = Modifier.fillMaxWidth()) {
            // Dropdown trigger
            Button(
                modifier = Modifier
                    .fillMaxWidth(),
                onClick = { isDropdownExpanded = !isDropdownExpanded }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Select $label",
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Dropdown for $label"
                    )
                }
            }

            // Dropdown menu for selecting places
            DropdownMenu(
                expanded = isDropdownExpanded,
                onDismissRequest = { isDropdownExpanded = false },
                modifier = Modifier.fillMaxWidth(),
                offset = DpOffset(x = 0.dp, y = 8.dp)
            ) {
                places.forEach { place ->
                    DropdownMenuItem(
                        text = { Text(place.name) },
                        onClick = {
                            onPlaceSelected(place) // Update selected place
                            isDropdownExpanded = false // Close dropdown
                        }
                    )
                }
            }
        }
    }
}

