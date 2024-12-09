package eric.triptales.components

import android.os.Bundle
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.*
import eric.triptales.api.PlaceResult
import eric.triptales.viewmodel.PlacesViewModel

/**
 * Displays a map screen with Google Maps, showing a target location and nearby attractions.
 *
 * This composable integrates Google Maps into Jetpack Compose using an [AndroidView].
 * It handles marker placement for the target location and nearby places,
 * and allows the user to select a place by clicking on a marker.
 *
 * @param placesViewModel The [PlacesViewModel] providing the target location and nearby places data.
 * @param navController The [NavController] used for navigation actions.
 */
@Composable
fun MapScreen(placesViewModel: PlacesViewModel, navController: NavController) {
    val context = LocalContext.current

    // Retain MapView state across recompositions
    val mapView = remember {
        MapView(context).apply {
            onCreate(Bundle())
            onResume()
        }
    }

    // Center marker representing the main location
    var centerMarker: Marker? by remember { mutableStateOf(null) }

    // Currently selected place, used for rendering the PlaceCard
    var selectedPlace by remember { mutableStateOf<PlaceResult?>(null) }

    // List of nearby places from the ViewModel
    val nearbyPlaces = placesViewModel.nearbyAttractions.value

    if (nearbyPlaces.isNotEmpty()) {
        Box(modifier = Modifier.fillMaxSize()) {
            /**
             * Integrate Google Maps into Jetpack Compose using AndroidView.
             */
            AndroidView(factory = { mapView }, update = {
                it.getMapAsync { googleMap ->
                    // Initialize Google Maps
                    MapsInitializer.initialize(context)

                    // Determine the default location (targetPlace or default to Paris)
                    val targetPlace = placesViewModel.targetPlace.value
                    val defaultLocation = if (targetPlace != null) {
                        LatLng(targetPlace.geometry.location.lat, targetPlace.geometry.location.lng)
                    } else {
                        LatLng(48.8566, 2.3522) // Paris coordinates
                    }

                    // Add the center marker to the map
                    if (centerMarker == null) {
                        centerMarker = googleMap.addMarker(
                            MarkerOptions()
                                .position(defaultLocation)
                                .title("Center Location")
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                        )
                        centerMarker?.tag = "center_marker"
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 17f))
                    }

                    // Add markers for nearby places
                    nearbyPlaces.forEach { place ->
                        val location = LatLng(place.geometry.location.lat, place.geometry.location.lng)
                        val marker = googleMap.addMarker(
                            MarkerOptions()
                                .position(location)
                                .title(place.name)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                        )
                        marker?.tag = place
                    }

                    // Handle marker click events
                    googleMap.setOnMarkerClickListener { marker ->
                        if (marker.tag == "center_marker" && targetPlace != null) {
                            // Handle center marker click
                            selectedPlace = PlaceResult(
                                place_id = targetPlace.place_id,
                                name = targetPlace.name,
                                geometry = targetPlace.geometry,
                                vicinity = targetPlace.formatted_address ?: "",
                                rating = targetPlace.rating
                            )
                        } else {
                            // Handle other marker clicks
                            val place = marker.tag as? PlaceResult
                            if (place?.place_id != null) {
                                selectedPlace = place
                            } else {
                                Log.e("MapScreen", "Marker clicked but placeId is null.")
                            }
                        }
                        true // Consume the event
                    }
                }
            })

            // Show a PlaceCard when a marker is selected
            selectedPlace?.let { place ->
                Box(modifier = Modifier.align(Alignment.BottomCenter)) {
                    PlaceCard(
                        place = place,
                        type = "nearby",
                        placesViewModel,
                        navController = navController
                    )
                }
            }
        }
    } else {
        // Show a placeholder or message if there are no nearby places
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("No nearby places found", color = Color.Gray)
        }
    }
}
