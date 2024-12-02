package eric.triptales.screens

import android.os.Bundle
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.*
import com.google.firebase.auth.FirebaseAuth
import eric.triptales.api.Route
import eric.triptales.components.BottomNavigationBar
import eric.triptales.firebase.entity.PlannedTrip
import eric.triptales.firebase.entity.SavedPlaceEntity
import eric.triptales.viewmodel.DirectionsViewModel

/**
 * Composable function for the Trip Detail screen.
 *
 * Displays details about a trip, including routes, map visualization, and trip-specific
 * information such as mode of transportation, waypoints, and trip summary.
 *
 * @param directionsViewModel The ViewModel containing data and logic for trip directions.
 * @param navController The NavController for navigating between screens.
 */
@Composable
fun TripDetailScreen(
    directionsViewModel: DirectionsViewModel,
    navController: NavController
) {
    // Observes the available routes for all travel modes from the ViewModel.
    val routesForModes by directionsViewModel.routesForModes.collectAsState()
    val userId = FirebaseAuth.getInstance().currentUser?.uid

    // List of transportation modes.
    val modes = listOf("driving", "walking", "bicycling", "transit")

    // Keeps track of the selected mode of transportation.
    var selectedMode by remember { mutableStateOf(modes.first()) }

    // Fetch selected route and error for the current mode
    val selectedResult = routesForModes[selectedMode]
    val selectedRoute = selectedResult?.first
    val errorMessage = selectedResult?.second

    // Observes the selected places and waypoints from the ViewModel.
    val selectedPlaces = directionsViewModel.selectedPlaces.collectAsState()
    val wayPointPlaces = directionsViewModel.waypoints.collectAsState()

    // State to manage the visibility of the dialog
    var showSaveDialog by remember { mutableStateOf(false) }
    var customTripName by remember { mutableStateOf("") }

    Scaffold(
        topBar = { eric.triptales.components.TopAppBar("Trip Detail", "sub", navController) },
        bottomBar = { BottomNavigationBar("Plan", navController) },
        floatingActionButton = {
            if (!directionsViewModel.tripDetailReadonly.value) {
                    FloatingActionButton(onClick = {
                        showSaveDialog = true
                    }) {
                        Icon(imageVector = Icons.Outlined.CheckCircle, contentDescription = "Save Trip")
                    }
            } else null
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            // Dropdown for selecting transportation mode.
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                var expanded by remember { mutableStateOf(false) }
                OutlinedButton(
                    onClick = { expanded = !expanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Mode: ${selectedMode.replaceFirstChar { it.uppercaseChar() }}")
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    modes.forEach { mode ->
                        DropdownMenuItem(
                            text = { Text(mode.replaceFirstChar { it.uppercaseChar() }) },
                            onClick = {
                                selectedMode = mode
                                expanded = false
                            }
                        )
                    }
                }
            }

            // Map Component
            selectedRoute?.let { route ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                ) {
                    TripDetailMap(route, selectedPlaces.value, wayPointPlaces.value)
                }
            }

            // Scrollable content for route information and trip details
            errorMessage?.let {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
                }
            } ?: run {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    selectedRoute?.let { route ->
                        Spacer(modifier = Modifier.height(16.dp))
                        RouteInfo(route, selectedMode)
                        Spacer(modifier = Modifier.height(16.dp))
                        RouteSteps(route)
                        Spacer(modifier = Modifier.height(16.dp))
                        TripDetailsList(selectedPlaces.value, wayPointPlaces.value)
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }

            if (showSaveDialog) {
                AlertDialog(
                    onDismissRequest = { showSaveDialog = false },
                    title = { Text("Save Trip") },
                    text = {
                        Column {
                            Text("Enter a custom name for the trip or skip to use the default name.")
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = customTripName,
                                onValueChange = { customTripName = it },
                                label = { Text("Trip Name") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                val tripName = if (customTripName.isNotBlank()) customTripName else {
                                    "${selectedPlaces.value.firstOrNull()?.name ?: "Unknown"} to ${selectedPlaces.value.lastOrNull()?.name ?: "Unknown"}"
                                }
                                saveTrip(directionsViewModel, selectedPlaces.value, wayPointPlaces.value, selectedRoute, userId, tripName)
                                showSaveDialog = false
                                navController.navigate("plan")
                            }
                        ) {
                            Text("Save")
                        }
                    },
                    dismissButton = {
                        OutlinedButton(
                            onClick = {
                                val defaultName = "${selectedPlaces.value.firstOrNull()?.name ?: "Unknown"} to ${selectedPlaces.value.lastOrNull()?.name ?: "Unknown"}"
                                saveTrip(directionsViewModel, selectedPlaces.value, wayPointPlaces.value, selectedRoute, userId, defaultName)
                                showSaveDialog = false
                                navController.navigate("plan")
                            }
                        ) {
                            Text("Skip")
                        }
                    }
                )
            }
        }
    }
}

/**
 * Composable function to display a map with trip details.
 *
 * @param route The selected route with polyline data.
 * @param selectedPlaces List of saved places, including origin and destination.
 * @param wayPointPlaces List of waypoint places.
 */
@Composable
fun TripDetailMap(route: Route, selectedPlaces: List<SavedPlaceEntity>, wayPointPlaces: List<SavedPlaceEntity>) {
    val context = LocalContext.current

    // Manages the lifecycle of the MapView.
    val mapView = remember {
        MapView(context).apply {
            onCreate(Bundle())
            onResume()
        }
    }

    var googleMap: GoogleMap? by remember { mutableStateOf(null) }

    val polylinePoints = route.overview_polyline.points.decodePath()

    AndroidView(factory = { mapView }, update = {
        it.getMapAsync { map ->
            googleMap = map
            map.clear()
            MapsInitializer.initialize(context)

            // Adds markers for origin, destination, and waypoints.
            selectedPlaces.getOrNull(0)?.let { origin ->
                map.addMarker(
                    MarkerOptions()
                        .position(LatLng(origin.latitude, origin.longitude))
                        .title("Origin")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                )
            }

            selectedPlaces.getOrNull(1)?.let { destination ->
                map.addMarker(
                    MarkerOptions()
                        .position(LatLng(destination.latitude, destination.longitude))
                        .title("Destination")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                )
            }

            wayPointPlaces.forEachIndexed { index, waypoint ->
                map.addMarker(
                    MarkerOptions()
                        .position(LatLng(waypoint.latitude, waypoint.longitude))
                        .title("Waypoint ${index + 1}")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
                )
            }

            // Draws the route polyline on the map.
            if (polylinePoints.isNotEmpty()) {
                map.addPolyline(
                    PolylineOptions()
                        .addAll(polylinePoints)
                        .color(android.graphics.Color.BLUE)
                        .width(10f)
                )
            }

            // Moves the camera to the origin or a default location.
            val cameraPosition = selectedPlaces.getOrNull(0)?.let { LatLng(it.latitude, it.longitude) }
                ?: LatLng(48.8566, 2.3522) // Defaults to Paris.
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(cameraPosition, 15f))
        }
    })
}

@Composable
fun RouteSteps(route: Route) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Steps to Complete Route", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            route.legs.forEachIndexed { legIndex, leg ->
                Text(
                    text = "Leg ${legIndex + 1}",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                leg.steps.forEachIndexed { stepIndex, step ->
                    Column(modifier = Modifier.padding(bottom = 8.dp)) {
                        Text(
                            text = "${stepIndex + 1}. ${step.html_instructions.replace("<[^>]*>".toRegex(), "")}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "Distance: ${step.distance.text} - Duration: ${step.duration.text}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}

/**
 * Composable function to display route information such as distance and duration.
 *
 * @param route The selected route containing distance and duration data.
 * @param selectedMode The selected mode of transportation.
 */
@Composable
fun RouteInfo(route: Route, selectedMode: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Route Information", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Distance: ${route.legs.sumOf { it.distance.value }} meters")
            Text("Duration: ${route.legs.sumOf { it.duration.value / 60 }} minutes")
            Text("Mode: $selectedMode")
        }
    }
}

/**
 * Displays a dynamic list of trip places including origin, waypoints, and destination.
 *
 * @param selectedPlaces List of origin and destination places.
 * @param waypoints List of waypoint places.
 */
@Composable
fun TripDetailsList(
    selectedPlaces: List<SavedPlaceEntity>,
    waypoints: List<SavedPlaceEntity>
) {
    // Build a combined list of origin, waypoints, and destination
    val places = buildList {
        selectedPlaces.getOrNull(0)?.let { add(it to "Origin") } // Add origin
        waypoints.forEachIndexed { index, waypoint ->
            add(waypoint to "Waypoint ${index + 1}")
        }
        selectedPlaces.getOrNull(1)?.let { add(it to "Destination") } // Add destination
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Trip Places", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            // Display each place with its details
            places.forEachIndexed { index, (place, label) ->
                PlaceDetails(index + 1, label, place)
                if (index != places.lastIndex) Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

/**
 * Displays details of a single place.
 *
 * @param index The position of the place in the list.
 * @param label The label (e.g., Origin, Waypoint 1, Destination).
 * @param place The SavedPlaceEntity containing place details.
 */
@Composable
fun PlaceDetails(index: Int, label: String, place: SavedPlaceEntity) {
    Column {
        Text("$index. $label: ${place.name}", style = MaterialTheme.typography.bodyLarge)
        if (place.address.isNotEmpty()) {
            Text("Address: ${place.address}", style = MaterialTheme.typography.bodyMedium)
        }
        if (!place.formattedPhoneNumber.isNullOrEmpty()) {
            Text("Phone: ${place.formattedPhoneNumber}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

/**
 * Extension function to decode a Google Maps polyline string into a list of LatLng points.
 *
 * @receiver Encoded polyline string.
 * @return List of LatLng points representing the polyline.
 */
fun String.decodePath(): List<LatLng> {
    val poly = mutableListOf<LatLng>()
    var index = 0
    val len = this.length
    var lat = 0
    var lng = 0

    while (index < len) {
        var b: Int
        var shift = 0
        var result = 0
        do {
            b = this[index++].code - 63
            result = result or (b and 0x1f shl shift)
            shift += 5
        } while (b >= 0x20)
        val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
        lat += dlat

        shift = 0
        result = 0
        do {
            b = this[index++].code - 63
            result = result or (b and 0x1f shl shift)
            shift += 5
        } while (b >= 0x20)
        val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
        lng += dlng

        val latLng = LatLng(lat / 1E5, lng / 1E5)
        poly.add(latLng)
    }

    return poly
}

/**
 * Saves the current trip to Firebase.
 */
private fun saveTrip(
    viewModel: DirectionsViewModel,
    selectedPlaces: List<SavedPlaceEntity>,
    waypoints: List<SavedPlaceEntity>,
    route: Route?,
    userId: String?,
    tripName: String
) {
    // Create a new PlannedTrip object
    val trip = PlannedTrip(
        tripId = System.currentTimeMillis().toString(),
        userId = userId ?: "",
        name = tripName,
        origin = selectedPlaces.firstOrNull() ?: return,
        destination = selectedPlaces.lastOrNull() ?: return,
        waypoints = waypoints,
        routeInfo = route?.let {
            eric.triptales.firebase.entity.RouteInfo(
                distance = it.legs.sumOf { leg -> leg.distance.value }
                    .toString(),
                duration = it.legs.sumOf { leg -> leg.duration.value }
                    .toString(),
                polyline = it.overview_polyline.points
            )
        }
    )

    // Save trip using the ViewModel
    viewModel.savePlannedTrip(trip)
}