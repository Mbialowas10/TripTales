package eric.triptales.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import eric.triptales.components.BottomNavigationBar
import eric.triptales.components.SavedPlaceCard
import eric.triptales.firebase.entity.SavedPlaceEntity
import eric.triptales.firebase.functions.fetchSavedStories
import eric.triptales.utility.parseCountry
import eric.triptales.viewmodel.DirectionsViewModel

/**
 * Composable function for the Place Picking screen.
 *
 * Allows the user to select an origin, destination, and waypoints for planning a trip.
 * Integrates with ViewModel to fetch and update selected places and fetch routes.
 *
 * @param directionViewModel ViewModel for managing direction data and logic.
 * @param navController NavController for navigating to other screens.
 */
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

    val selectedCountry by remember {
        derivedStateOf {
            selectedPlaces.firstOrNull()?.let { parseCountry(it.address) }
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
                    // Origin Place Item
                    PlaceItem(
                        label = "Origin",
                        places = remainingPlaces.value,
                        selectedPlace = selectedPlaces.getOrNull(0),
                        onPlaceSelected = { place ->
                            if (selectedPlaces.size > 0) {
                                selectedPlaces[0] = place
                            } else {
                                selectedPlaces.add(place)
                            }
                        },
                        selectedCountry ?: ""
                    )
                }

                items(waypoints.size) { index ->
                    // Waypoint Place Items
                    PlaceItem(
                        label = "Waypoint ${index + 1}",
                        places = remainingPlaces.value,
                        selectedPlace = waypoints.getOrNull(index),
                        onPlaceSelected = { place -> waypoints[index] = place },
                        selectedCountry ?: ""
                    )
                }

                item {
                    // Destination Place Item
                    PlaceItem(
                        label = "Destination",
                        places = remainingPlaces.value,
                        selectedPlace = selectedPlaces.getOrNull(1),
                        onPlaceSelected = { place ->
                            if (selectedPlaces.size > 1) {
                                selectedPlaces[1] = place
                            } else {
                                selectedPlaces.add(place)
                            }
                        },
                        selectedCountry ?: ""
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
                        directionViewModel.tripDetailReadonly.value = false
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

/**
 * Composable function to display a dropdown for selecting a place.
 *
 * Groups places by their country and provides an interactive dropdown menu
 * for selecting a place as an origin, waypoint, or destination.
 *
 * @param label Label for the dropdown (e.g., "Origin", "Destination").
 * @param places List of available places to select from.
 * @param selectedPlace The currently selected place (if any).
 * @param onPlaceSelected Callback invoked when a place is selected.
 * @param selectedCountry The currently selected country to highlight in the dropdown.
 */
@Composable
fun PlaceItem(
    label: String,
    places: List<SavedPlaceEntity>,
    selectedPlace: SavedPlaceEntity?,
    onPlaceSelected: (SavedPlaceEntity) -> Unit,
    selectedCountry: String
) {
    var isDropdownExpanded by remember { mutableStateOf(false) }

    // Group places by country
    val sortedPlaces = places.groupBy { parseCountry(it.address) }

    Column(modifier = Modifier.fillMaxWidth().padding(4.dp)) {
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
                // Header Row with Cancel Button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Select $label",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = { isDropdownExpanded = false }, // Close dropdown
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Dropdown"
                        )
                    }
                }

                sortedPlaces.forEach { (country, countryPlaces) ->
                    // Country Header
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = country,
                                style = MaterialTheme.typography.titleMedium,
                                color = if (country == selectedCountry)
                                    MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurface
                            )
                        },
                        onClick = { } // No action for the header
                    )

                    // Places under this country
                    countryPlaces.forEach { place ->
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
}


