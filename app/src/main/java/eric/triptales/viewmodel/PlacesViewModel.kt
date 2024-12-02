package eric.triptales.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import eric.triptales.api.Geometry
import eric.triptales.api.Location
import eric.triptales.api.PlaceDetailResult
import eric.triptales.api.PlaceResult
import eric.triptales.api.RetrofitInstance
import eric.triptales.database.AppDatabase
import eric.triptales.database.PlaceEntity
import eric.triptales.database.StoryEntity
import eric.triptales.firebase.entity.SavedPlaceEntity
import eric.triptales.screens.deleteImagesForPlace
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class PlacesViewModel(application: Application) : AndroidViewModel(application) {
    private val placeDao = AppDatabase.getDatabase(application).placeDao()
    private val storyDao = AppDatabase.getDatabase(application).storyDao()
    private val API_KEY = "AIzaSyBQtniS0NCgJc5D5g_t_ke42u5_ttYn4Rw"

    val db = FirebaseFirestore.getInstance()

    val nearbyAttractions = mutableStateOf<List<PlaceResult>>(listOf())
    val autocompleteResults = mutableStateOf<List<PlaceResult>>(listOf())
    val savedPlaces = mutableStateOf<List<PlaceEntity>>(listOf())
    val savedStories = mutableStateOf<List<StoryEntity>>(listOf())

    // handle loading
    val isFetchDetail = mutableStateOf(false)
    val isFetchNearBy = mutableStateOf(false)

    // Search
    private val _searchTerm = mutableStateOf("")
    val searchQuery: State<String> = _searchTerm

    val targetPlace = mutableStateOf<PlaceDetailResult?>(null)
    val targetDBPlace = mutableStateOf<PlaceEntity?>(null)
    val isSaved = mutableStateOf(false)

    // return list of suggestions with place ID for further search
    fun findPlaceAutocomplete(searchTerm: String, placeTypes: String) {
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
                        photos = result.photos,
                        reviews = result.reviews
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
                    radius = 50000,
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

                // Upload the place to Firebase
                val localPlaceId = placeEntity.id
                val localUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                val savedPlaceEntry = SavedPlaceEntity(
                    placeId = localPlaceId,
                    userId = localUserId,
                    documentId = "${localPlaceId}_$localUserId",
                    name = placeEntity.name,
                    latitude = placeEntity.latitude,
                    longitude = placeEntity.longitude,
                    rating = placeEntity.rating,
                    address = placeEntity.address,
                    category = placeEntity.category,
                    formattedPhoneNumber = placeEntity.formatted_phone_number,
                    website = placeEntity.website,
                    photos = placeEntity.photos,
                )
                db.collection("saved_places")
                    .add(savedPlaceEntry)

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
                val documentId = "${placeDetailResult.place_id}_${FirebaseAuth.getInstance().currentUser?.uid}"

                db.collection("saved_places")
                    .whereEqualTo("documentId",documentId)
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        if (!querySnapshot.isEmpty) {
                            // Get the actual document ID from the query result
                            val localDocumentId = querySnapshot.documents[0].id

                            // Delete the document using the actual document ID
                            db.collection("saved_places")
                                .document(localDocumentId)
                                .delete()
                        }
                    }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun deletePlaceFromDB(context: Context ,id: String){
        viewModelScope.launch {
            try {
                placeDao.deleteById(id)
                deleteImagesForPlace(context ,id)
                getAllPlacesFromDB()
                val documentId = "${id}_${FirebaseAuth.getInstance().currentUser?.uid}"
                db.collection("saved_places")
                    .whereEqualTo("documentId",documentId)
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        if (!querySnapshot.isEmpty) {
                            // Get the actual document ID from the query result
                            val localDocumentId = querySnapshot.documents[0].id

                            // Delete the document using the actual document ID
                            db.collection("saved_places")
                                .document(localDocumentId)
                                .delete()
                        }
                    }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getAllPlacesFromDB(){
        viewModelScope.launch {
            try{
                savedPlaces.value = placeDao.getAllPlaces()
            }catch (e: Exception){
                e.printStackTrace()
            }
        }
    }

    fun setTargetDBPlace(id: String){
        viewModelScope.launch {
            try{
                targetDBPlace.value = getPlace(id)
            }catch (e: Exception){
                e.printStackTrace()
            }
        }
    }

    fun getStoriesForPlace(placeId: String){
        viewModelScope.launch(Dispatchers.IO) {
            try{
                savedStories.value = storyDao.getStoriesForPlace(placeId)

            }catch (e: Exception){
                e.printStackTrace()
            }
        }
    }

    fun addStory(story: StoryEntity) {
        viewModelScope.launch {
            storyDao.insertStory(story)

            getStoriesForPlace(story.place_id)
        }
    }

    fun deleteStory(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            storyDao.deleteStory(id)

            savedStories.value = storyDao.getStoriesForPlace(savedStories.value.firstOrNull()?.place_id ?: "")
        }
    }
}