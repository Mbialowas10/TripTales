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
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import eric.triptales.api.PlaceResult
import eric.triptales.viewmodel.PlacesViewModel

@Composable
fun MapScreen(placesViewModel: PlacesViewModel, navController: NavController) {
    val context = LocalContext.current

    // use remember to retain its state across recompositions
    val mapView = remember {
        MapView(context).apply {
            onCreate(Bundle())
            onResume()
        }
    }

    // Store the center marker
    var centerMarker: Marker? by remember { mutableStateOf(null) }

    // Store the selected place for rendering the PlaceCard, does not affect map rendering
    var selectedPlace by remember { mutableStateOf<PlaceResult?>(null) }

    // Check if `nearbyAttractions` is not null or empty before rendering the map
    val nearbyPlaces = placesViewModel.nearbyAttractions.value

    if (nearbyPlaces.isNotEmpty()) {
        Box(modifier = androidx.compose.ui.Modifier.fillMaxSize()) {
            // Since Jetpack Compose does not have built-in support for GG Map
            // => Need to use AndroidView to integrate the traditional MapView
            AndroidView(factory = { mapView }, update = {
                it.getMapAsync { googleMap ->
                    // Initialize Google Maps
                    MapsInitializer.initialize(context)

                    // Get the center location
                    val targetPlace = placesViewModel.targetPlace.value
                    val defaultLocation = if (targetPlace != null) {
                        LatLng(targetPlace.geometry.location.lat, targetPlace.geometry.location.lng)
                    } else {
                        // Default to Paris coordinates
                        LatLng(48.8566, 2.3522)
                    }

                    // Add center marker
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

                    // Add red markers for nearby places
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

                    // Handle marker click event without re-rendering the map or moving the camera
                    googleMap.setOnMarkerClickListener { marker ->
                        if (marker.tag == "center_marker" && targetPlace != null) {
                            // Show a placeholder PlaceResult or center marker details in PlaceCard
                            selectedPlace = PlaceResult(
                                place_id = targetPlace.place_id,
                                name = targetPlace.name,
                                geometry = targetPlace.geometry,
                                vicinity = targetPlace.formatted_address ?: "",
                                rating = targetPlace.rating
                            )
                        } else {
                            // Handle other markers' clicks
                            val place = marker.tag as? PlaceResult
                            if (place?.place_id != null) {
                                selectedPlace = place
                            } else {
                                Log.e("MapScreen", "Marker clicked but placeId is null.")
                            }
                        }

                        true
                    }
                }
            })

            // Show the PlaceCard when a marker is clicked
            selectedPlace?.let { place ->
                Box(modifier = androidx.compose.ui.Modifier.align(androidx.compose.ui.Alignment.BottomCenter)) {
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
        // Could show a message indicating that there are no nearby places.
    }
}
