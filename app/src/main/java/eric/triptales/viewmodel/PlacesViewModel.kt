package eric.triptales.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import eric.triptales.BuildConfig
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
import eric.triptales.utility.ToastUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * ViewModel for managing places and stories.
 *
 * Handles fetching, saving, and managing data for places and stories using
 * both a Room database and Firebase Firestore. Provides methods for managing
 * user interactions with places and stories.
 *
 * @param application The application instance used for initializing Room database.
 */
class PlacesViewModel(application: Application) : AndroidViewModel(application) {
    private val placeDao = AppDatabase.getDatabase(application).placeDao()
    private val storyDao = AppDatabase.getDatabase(application).storyDao()
    private val API_KEY = BuildConfig.GOOGLE_MAPS_API_KEY

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

    /**
     * Fetches autocomplete suggestions for places based on the search term.
     *
     * @param searchTerm The term to search for places.
     * @param placeTypes The types of places to include in the results.
     */
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

    /**
     * Fetches details for a specific place and optionally fetches nearby attractions.
     *
     * @param placeId The ID of the place to fetch details for.
     * @param isSearchNearBy Whether to fetch nearby attractions after fetching place details.
     */
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

    /**
     * Fetches nearby attractions based on latitude and longitude.
     *
     * @param lat The latitude of the location.
     * @param lng The longitude of the location.
     */
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

    /**
     * Saves a place to the local Room database and uploads it to Firebase Firestore.
     *
     * @param placeDetailResult The details of the place to save.
     */
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
            } finally {
                ToastUtil.showToast(null, "Place saved successfully!")
            }
        }
    }

    /**
     * Fetches a place from the local database by its ID.
     *
     * @param id The ID of the place to fetch.
     * @return The `PlaceEntity` object if found, or `null` if an exception occurs.
     */
    private suspend fun getPlace(id: String): PlaceEntity? {
        return try {
            placeDao.getPlace(id)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Checks if a place is saved in the local database and updates the `isSaved` state.
     *
     * @param id The ID of the place to check.
     */
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

    /**
     * Toggles the saved state of a place.
     *
     * If the place is already saved, it is deleted. Otherwise, it is saved to the local database and Firebase.
     *
     * @param placeDetailResult The details of the place to toggle.
     */
    fun toggleSavePlace(placeDetailResult: PlaceDetailResult){
        viewModelScope.launch {
            try{
                if(isSaved.value){
                    deleteTargetPlaceFromDB(placeDetailResult)
                    isSaved.value = false
                    ToastUtil.showToast(null, "Place deleted successfully: ${placeDetailResult.name}")
                    Log.e("PlacesViewModel", "Place deleted successfully: ${placeDetailResult.name}")
                } else {
                    saveTargetPlaceToDB(placeDetailResult)
                    isSaved.value = true
                    ToastUtil.showToast(null, "Place saved successfully: ${placeDetailResult.name}")
                    Log.e("PlacesViewModel", "Place saved successfully: ${placeDetailResult.name}")
                }
            }catch (e: Exception){
                e.printStackTrace()
            }
        }
    }

    /**
     * Deletes a place from the local database and Firebase Firestore.
     *
     * @param placeDetailResult The details of the place to delete.
     */
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

    /**
     * Deletes a place from the local database, Firebase Firestore, and associated images.
     *
     * @param context The application context for deleting images.
     * @param id The ID of the place to delete.
     */
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

                            ToastUtil.showToast(null, "Place unsaved successfully!")
                        }
                    }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Fetches all saved places from the local database and updates the state.
     */
    fun getAllPlacesFromDB(){
        viewModelScope.launch {
            try{
                savedPlaces.value = placeDao.getAllPlaces()
            }catch (e: Exception){
                e.printStackTrace()
            }
        }
    }

    /**
     * Sets the `targetDBPlace` state to the place retrieved from the local database.
     *
     * @param id The ID of the place to retrieve.
     */
    fun setTargetDBPlace(id: String){
        viewModelScope.launch {
            try{
                targetDBPlace.value = getPlace(id)
            }catch (e: Exception){
                e.printStackTrace()
            }
        }
    }


    /**
     * Fetches all stories associated with a specific place ID and updates the state.
     *
     * @param placeId The ID of the place whose stories are to be fetched.
     */
    fun getStoriesForPlace(placeId: String){
        viewModelScope.launch(Dispatchers.IO) {
            try{
                savedStories.value = storyDao.getStoriesForPlace(placeId)

            }catch (e: Exception){
                e.printStackTrace()
            }
        }
    }

    /**
     * Adds a new story to the local database and updates the stories for the associated place.
     *
     * @param story The `StoryEntity` to be added.
     */
    fun addStory(story: StoryEntity) {
        viewModelScope.launch {
            try{
                storyDao.insertStory(story)
            } catch (e: Exception){
                e.printStackTrace()
            } finally {
                getStoriesForPlace(story.place_id)
                ToastUtil.showToast(null, "Story shared successfully!")
            }
        }
    }

    /**
     * Deletes a story from the local database by its ID and updates the state.
     *
     * @param id The ID of the story to delete.
     */

    fun deleteStory(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            storyDao.deleteStory(id)

            savedStories.value = storyDao.getStoriesForPlace(savedStories.value.firstOrNull()?.place_id ?: "")
        }
    }
}