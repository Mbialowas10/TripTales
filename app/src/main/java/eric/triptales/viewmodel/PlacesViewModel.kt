package eric.triptales.viewmodel

import android.app.Application
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import eric.triptales.api.Geometry
import eric.triptales.api.Location
import eric.triptales.api.PlaceDetailResult
import eric.triptales.api.PlaceResult
import eric.triptales.api.RetrofitInstance
import eric.triptales.database.AppDatabase
import eric.triptales.database.PlaceEntity
import kotlinx.coroutines.launch


class PlacesViewModel(application: Application) : AndroidViewModel(application) {
    private val placeDao = AppDatabase.getDatabase(application).placeDao()
    private val API_KEY = "AIzaSyBQtniS0NCgJc5D5g_t_ke42u5_ttYn4Rw"

    val nearbyAttractions = mutableStateOf<List<PlaceResult>>(listOf())
    val autocompleteResults = mutableStateOf<List<PlaceResult>>(listOf())

    // Search
    private val _searchTerm = mutableStateOf("")
    val searchQuery: State<String> = _searchTerm

    val targetPlace = mutableStateOf<PlaceDetailResult?>(null)

    // return list of suggestions with place ID for further search
    fun findPlaceAutocomplete(searchTerm: String, placeTypes: String = "establishment") {
        _searchTerm.value = searchTerm
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getAutocompleteResults(
                    input = searchTerm,
                    types = placeTypes,
                    apiKey = API_KEY
                )

                autocompleteResults.value = response.predictions.map { prediction ->
                    PlaceResult(
                        name = prediction.description,
                        placeId = prediction.place_id,
                        geometry = Geometry(Location(0.0, 0.0)),
                        rating = 0.0,
                        vicinity = prediction.description
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getPlaceDetail(placeId: String){
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getPlaceDetails(
                    placeId = placeId,
                    apiKey = API_KEY
                )
                val latitude = response.result.geometry.location.lat
                val longitude = response.result.geometry.location.lng

                targetPlace.value = response.result

                savePlaceToDB(response.result)

                // Now use latitude and longitude for your nearby search
                getNearbyAttractions(latitude, longitude)


            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun getNearbyAttractions(lat: Double, lng: Double) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getNearbyAttractions(
                    location = "$lat,$lng",
                    radius = 5000,
                    apiKey = API_KEY
                )
                nearbyAttractions.value = response.results
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun savePlaceToDB(placeDetailResult: PlaceDetailResult){
        viewModelScope.launch {
            try {
                // Convert PlaceResult to PlaceEntity
                val placeEntity = PlaceEntity(
                    id = placeDetailResult.place_id,
                    name = placeDetailResult.name,
                    latitude = placeDetailResult.geometry.location.lat,
                    longitude = placeDetailResult.geometry.location.lng,
                    rating = placeDetailResult.rating,
                    address = placeDetailResult.formatted_address ?: "",
                    category = placeDetailResult.types ?: emptyList() ,
                    formatted_phone_number = placeDetailResult.formatted_phone_number ?: "",
                    website = placeDetailResult.website ?: "",
                    is_saved = true,
                    saved_at = System.currentTimeMillis()
                )

                // Insert the PlaceEntity into the Room database
                placeDao.insertAll(listOf(placeEntity))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}