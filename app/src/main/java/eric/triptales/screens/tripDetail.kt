package eric.triptales.screens

import android.os.Bundle
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.*
import eric.triptales.api.Route
import eric.triptales.components.BottomNavigationBar
import eric.triptales.firebase.SavedPlaceEntity
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

    // List of transportation modes.
    val modes = listOf("driving", "walking", "bicycling", "transit")

    // Keeps track of the selected mode of transportation.
    var selectedMode by remember { mutableStateOf(modes.first()) }

    // Fetches the route corresponding to the selected mode.
    val selectedRoute = routesForModes[selectedMode]

    // Observes the selected places and waypoints from the ViewModel.
    val selectedPlaces = directionsViewModel.selectedPlaces.collectAsState()
    val wayPointPlaces = directionsViewModel.waypoints.collectAsState()

    Scaffold(
        topBar = { eric.triptales.components.TopAppBar("Planning a Trip", "sub", navController) },
        bottomBar = { BottomNavigationBar("Plan", navController) },
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

            // Displays the selected route's map and trip details if a route is available.
            selectedRoute?.let { route ->
                Column(modifier = Modifier.fillMaxSize()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        TripDetailMap(route, selectedPlaces.value, wayPointPlaces.value)
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    RouteInfo(route, selectedMode)
                    Spacer(modifier = Modifier.height(16.dp))
                    TripDetails(places = listOf("Origin", "Waypoint", "Destination"))
                }
            } ?: run {
                // Displays a loading indicator if no route is available.
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
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
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(cameraPosition, 10f))
        }
    })
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
            Text("Duration: ${route.legs.sumOf { it.duration.value }} seconds")
            Text("Mode: $selectedMode")
        }
    }
}

@Composable
fun TripDetails(places: List<String>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Trip Places", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            places.forEachIndexed { index, place ->
                Text("${index + 1}. $place", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(4.dp))
            }
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
