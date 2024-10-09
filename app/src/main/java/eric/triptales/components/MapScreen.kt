package eric.triptales.components

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import eric.triptales.viewmodel.PlacesViewModel

@Composable
fun MapScreen(placesViewModel: PlacesViewModel) {
    val context = LocalContext.current
    val mapView = remember {
        MapView(context).apply {
            onCreate(Bundle())
            onResume()
            getMapAsync { googleMap ->
                // Initialize Google Maps
                MapsInitializer.initialize(context)

                // Zoom to a default location (Paris example coordinates)
                val defaultLocation = LatLng(48.8566, 2.3522)
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 10f))

                // Adding markers for nearby attractions
                val nearbyPlaces = placesViewModel.nearbyAttractions.value
                nearbyPlaces.forEach { place ->
                    val location = LatLng(place.geometry.location.lat, place.geometry.location.lng)
                    googleMap.addMarker(
                        MarkerOptions().position(location).title(place.name)
                    )
                }
            }
        }
    }

    AndroidView(factory = { mapView }, update = {
        it.onResume()
    })
}