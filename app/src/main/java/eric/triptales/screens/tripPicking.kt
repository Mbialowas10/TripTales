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

@Composable
fun PlacePickingScreen(
    directionViewModel: DirectionsViewModel,
    navController: NavController
) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid

    val selectedPlaces = remember { mutableStateListOf<SavedPlaceEntity>() }
    var savedPlaces by remember { mutableStateOf<List<SavedPlaceEntity>>(emptyList()) }
    val waypoints = remember { mutableStateListOf<SavedPlaceEntity>() }
    val remainingPlaces = remember {
        derivedStateOf {
            savedPlaces.filter { it !in selectedPlaces && it !in waypoints }
        }
    }

    LaunchedEffect(userId) {
        userId?.let {
            fetchSavedStories(
                userId = it,
                onSuccess = { places -> savedPlaces = places },
                onFailure = { /* Show error message */ }
            )
        }
    }

    Scaffold(
        topBar = { eric.triptales.components.TopAppBar("Planning a Trip", "sub", navController) },
        bottomBar = { BottomNavigationBar("Plan", navController) },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 8.dp)
            ) {
                item {
                    PlaceItem(
                        label = "Origin",
                        places = remainingPlaces.value,
                        selectedPlace = selectedPlaces.getOrNull(0),
                        onPlaceSelected = { place ->
                            if (selectedPlaces.size >= 1) selectedPlaces[0] = place
                            else selectedPlaces.add(place)
                        }
                    )
                }

                items(waypoints.size) { index ->
                    PlaceItem(
                        label = "Waypoint ${index + 1}",
                        places = remainingPlaces.value,
                        selectedPlace = waypoints.getOrNull(index),
                        onPlaceSelected = { place -> waypoints[index] = place }
                    )
                }

                item {
                    PlaceItem(
                        label = "Destination",
                        places = remainingPlaces.value,
                        selectedPlace = selectedPlaces.getOrNull(1),
                        onPlaceSelected = { place ->
                            if (selectedPlaces.size >= 2) selectedPlaces[1] = place
                            else selectedPlaces.add(place)
                        }
                    )
                }


            }

            Button(
                onClick = { waypoints.add(SavedPlaceEntity(placeId = "", name = "Select Place")) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text("Add Waypoint")
            }

            Button(
                onClick = {
                    if (selectedPlaces.size >= 2) {
                        val origin = selectedPlaces[0].placeId
                        val destination = selectedPlaces[1].placeId
                        val waypointIds = waypoints.map { it.placeId }
                        directionViewModel.updateSelectedPlaces(selectedPlaces[0], selectedPlaces[1], waypoints)
                        directionViewModel.fetchRoutes(origin, destination, waypointIds)
                        navController.navigate("tripDetail")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                enabled = selectedPlaces.size >= 2
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
    selectedPlace: SavedPlaceEntity?,
    onPlaceSelected: (SavedPlaceEntity) -> Unit
) {
    var isDropdownExpanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text(
            text = label,
            modifier = Modifier.padding(start = 8.dp),
            style = MaterialTheme.typography.bodyMedium
        )

        Box(modifier = Modifier.fillMaxWidth()) {
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = { isDropdownExpanded = !isDropdownExpanded }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = selectedPlace?.name ?: "Select $label",
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Dropdown for $label"
                    )
                }
            }

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
                            onPlaceSelected(place)
                            isDropdownExpanded = false
                        }
                    )
                }
            }
        }
    }
}
