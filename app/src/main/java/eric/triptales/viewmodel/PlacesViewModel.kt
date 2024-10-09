package eric.triptales.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import eric.triptales.api.Geometry
import eric.triptales.api.Location
import eric.triptales.api.PlaceResult
import eric.triptales.api.RetrofitInstance
import eric.triptales.database.AppDatabase
import eric.triptales.database.PlaceEntity
import kotlinx.coroutines.launch


class PlacesViewModel(application: Application) : AndroidViewModel(application) {
    private val placeDao = AppDatabase.getDatabase(application).placeDao()
    val nearbyAttractions = mutableStateOf<List<PlaceResult>>(listOf())

    fun getNearbyAttractions(lat: Double, lng: Double, apiKey: String) {
        viewModelScope.launch {
            try {
                // Step 1: Fetch data from Room database (local cache)
                val cachedPlaces = placeDao.getPlacesByLocation(lat, lng)
                if (cachedPlaces.isNotEmpty()) {
                    // Use cached data if available
                    nearbyAttractions.value = cachedPlaces.map {
                        PlaceResult(it.name, Geometry(Location(it.latitude, it.longitude)), it.rating, it.address)
                    }
                } else {
                    // Step 2: Fetch data from Google Places API if no cache available
                    val response = RetrofitInstance.api.getNearbyAttractions(
                        location = "$lat,$lng",
                        radius = 5000,
                        apiKey = apiKey
                    )
                    nearbyAttractions.value = response.results

                    // Step 3: Cache the results in Room database
                    val placesToCache = response.results.map { place ->
                        PlaceEntity(
                            id = "${place.geometry.location.lat}_${place.geometry.location.lng}",
                            name = place.name,
                            latitude = place.geometry.location.lat,
                            longitude = place.geometry.location.lng,
                            rating = place.rating,
                            address = place.vicinity,
                            timestamp = System.currentTimeMillis()
                        )
                    }
                    placeDao.insertAll(placesToCache)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}