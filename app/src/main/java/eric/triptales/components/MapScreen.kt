package eric.triptales.components

import android.os.Bundle
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import eric.triptales.api.PlaceResult
import eric.triptales.viewmodel.PlacesViewModel

@Composable
fun MapScreen(placesViewModel: PlacesViewModel, navController: NavController) {
    val context = LocalContext.current
    val mapView = remember {
        MapView(context).apply {
            onCreate(Bundle())
            onResume()
        }
    }

    // Store the center marker only once to prevent re-render
    var centerMarker: Marker? by remember { mutableStateOf(null) }

    // Store the selected place for rendering the PlaceCard, does not affect map rendering
    var selectedPlace by remember { mutableStateOf<PlaceResult?>(null) }

    // Check if `nearbyAttractions` is not null or empty before rendering the map
    val nearbyPlaces = placesViewModel.nearbyAttractions.value
    if (nearbyPlaces.isNotEmpty()) {
        Box(modifier = androidx.compose.ui.Modifier.fillMaxSize()) {
            AndroidView(factory = { mapView }, update = {
                it.getMapAsync { googleMap ->
                    // Initialize Google Maps
                    MapsInitializer.initialize(context)

                    // Get the center location
                    val targetPlace = placesViewModel.targetPlace.value
                    val defaultLocation = if (targetPlace != null) {
                        LatLng(targetPlace.geometry.location.lat, targetPlace.geometry.location.lng)
                    } else {
                        LatLng(48.8566, 2.3522) // Default to Paris coordinates
                    }

                    // Add or update center marker without re-rendering it on each click
                    if (centerMarker == null) {
                        centerMarker = googleMap.addMarker(
                            MarkerOptions()
                                .position(defaultLocation)
                                .title("Center Location")
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                        )
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 17f))
                    }

                    // Add red markers for nearby places
                    nearbyPlaces.forEach { place ->
                        val location = LatLng(place.geometry.location.lat, place.geometry.location.lng)

                        // Add a default red marker for each nearby place
                        val marker = googleMap.addMarker(
                            MarkerOptions()
                                .position(location)
                                .title(place.name)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                        )

                        // Set marker tag as the place for later identification
                        marker?.tag = place
                    }

                    // Handle marker click event without re-rendering the map or moving the camera
                    googleMap.setOnMarkerClickListener { marker: Marker ->
                        val place = marker.tag as? PlaceResult
                        if (place != null) {
                            selectedPlace = place // Update the selected place for the PlaceCard
                        }
                        true // Prevent default camera movement
                    }
                }
            })

            // Show the PlaceCard when a marker is clicked
            selectedPlace?.let { place ->
                Box(modifier = androidx.compose.ui.Modifier.align(androidx.compose.ui.Alignment.BottomCenter)) {
                    PlaceCard(
                        place = place,
                        type = "nearby",
                        onSearchNearbyClick = { placeId ->
                            // Handle "Search Nearby" button click
                            placesViewModel.getPlaceDetail(placeId)
                        },
                        navController = navController
                    )
                }
            }
        }
    } else {
        // Optionally, you could display a loading spinner or message if nearbyAttractions is empty
        // or show a message indicating that there are no nearby places.
    }
}
