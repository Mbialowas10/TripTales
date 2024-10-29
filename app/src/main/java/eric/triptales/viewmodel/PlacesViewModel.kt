package eric.triptales.viewmodel

import android.app.Application
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
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

    // handle loading
    val isFetchDetail = mutableStateOf(false)
    val isFetchNearBy = mutableStateOf(false)

    // Search
    private val _searchTerm = mutableStateOf("")
    val searchQuery: State<String> = _searchTerm

    val targetPlace = mutableStateOf<PlaceDetailResult?>(null)
    val isSaved = mutableStateOf(false)

    // return list of suggestions with place ID for further search
    fun findPlaceAutocomplete(searchTerm: String, placeTypes: String = "geocode") {
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
                        place_id = prediction.place_id,
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

    fun getPlaceDetail(placeId: String, isSearchNearBy: Boolean){
        viewModelScope.launch {
            isFetchDetail.value = true
            try {
                val response = RetrofitInstance.api.getPlaceDetails(
                    placeId = placeId,
                    apiKey = API_KEY
                )

                targetPlace.value = response.result.let { result ->
                    PlaceDetailResult(
                        place_id = result.place_id,
                        name = result.name,
                        geometry = result.geometry,
                        rating = result.rating,
                        formatted_address = result.formatted_address,
                        types = result.types,
                        formatted_phone_number = result.formatted_phone_number,
                        website = result.website,
                        photos = result.photos
                    )
                }

                // savePlaceToDB(response.result)
                if(isSearchNearBy){
                    val latitude = response.result.geometry.location.lat
                    val longitude = response.result.geometry.location.lng

                    getNearbyAttractions(latitude, longitude)
                }

            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isFetchDetail.value = false
            }
        }
    }

    private fun getNearbyAttractions(lat: Double, lng: Double) {
        viewModelScope.launch {
            isFetchNearBy.value = true
            try {
                val response = RetrofitInstance.api.getNearbyAttractions(
                    location = "$lat,$lng",
                    radius = 10000,
                    apiKey = API_KEY
                )
                nearbyAttractions.value = response.results.map { result ->
                    PlaceResult(
                        name = result.name,
                        place_id = result.place_id,
                        geometry = result.geometry,
                        rating = result.rating,
                        vicinity = result.vicinity
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isFetchNearBy.value = false
            }
        }
    }

    fun saveTargetPlaceToDB(placeDetailResult: PlaceDetailResult){
        viewModelScope.launch {
            try {
                val photoReferences = placeDetailResult.photos?.map { photo ->
                    photo.photo_reference
                } ?: listOf()

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
                    photos = photoReferences,
                    saved_at = System.currentTimeMillis()
                )

                // Insert the PlaceEntity into the Room database
                placeDao.insertAll(listOf(placeEntity))
                Log.e("PlacesViewModel", "Place saved to database: ${placeDetailResult.name}")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private suspend fun getPlace(id: String): PlaceEntity? {
        return try {
            placeDao.getPlace(id)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun checkIfPlaceSaved(id: String){
        viewModelScope.launch {
            try{
                getPlace(id)
                isSaved.value = getPlace(id) != null
            }catch (e: Exception){
                e.printStackTrace()
            }
        }
    }

    fun toggleSavePlace(placeDetailResult: PlaceDetailResult){
        viewModelScope.launch {
            try{
                if(isSaved.value){
                    deleteTargetPlaceFromDB(placeDetailResult)
                    isSaved.value = false
                    Log.e("PlacesViewModel", "Place deleted successfully: ${placeDetailResult.name}")
                } else {
                    saveTargetPlaceToDB(placeDetailResult)
                    isSaved.value = true
                    Log.e("PlacesViewModel", "Place saved successfully: ${placeDetailResult.name}")
                }
            }catch (e: Exception){
                e.printStackTrace()
            }
        }
    }

    fun deleteTargetPlaceFromDB(placeDetailResult: PlaceDetailResult){
        viewModelScope.launch {
            try {
                placeDao.deleteById(placeDetailResult.place_id)
                Log.e("PlacesViewModel", "Place deleted from database: ${placeDetailResult.name}")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}