package eric.triptales.viewmodel

import android.app.Application
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import eric.triptales.api.DirectionsApiService
import eric.triptales.api.DirectionsResponse
import eric.triptales.api.Route
import eric.triptales.api.RetrofitInstance
import eric.triptales.firebase.entity.PlannedTrip
import eric.triptales.firebase.entity.SavedPlaceEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

/**
 * ViewModel to handle fetching routes and managing state for the Trip Detail screen.
 *
 * This ViewModel interacts with the Directions API to fetch routes for various travel modes
 * (e.g., driving, walking, bicycling, transit) and manages the state of selected places and waypoints.
 *
 * @param application The application context, required by AndroidViewModel.
 */
class DirectionsViewModel(application: Application) : AndroidViewModel(application) {
    private val API_KEY = "AIzaSyBQtniS0NCgJc5D5g_t_ke42u5_ttYn4Rw"
    val tripDetailReadonly = mutableStateOf(false)

    // StateFlow to hold routes and errors for each mode
    private val _routesForModes = MutableStateFlow<Map<String, Pair<Route?, String?>>>(emptyMap())
    val routesForModes: StateFlow<Map<String, Pair<Route?, String?>>> get() = _routesForModes

    // List to hold planned trips
    val plannedTrips = mutableListOf<PlannedTrip>()

    // StateFlow to manage the selected origin and destination places
    private val _selectedPlaces = MutableStateFlow<List<SavedPlaceEntity>>(emptyList())
    val selectedPlaces: StateFlow<List<SavedPlaceEntity>> get() = _selectedPlaces

    // StateFlow to manage the waypoints for a trip
    private val _waypoints = MutableStateFlow<List<SavedPlaceEntity>>(emptyList())
    val waypoints: StateFlow<List<SavedPlaceEntity>> get() = _waypoints

    /**
     * Updates the selected origin, destination, and waypoints.
     *
     * @param origin The selected origin place.
     * @param destination The selected destination place.
     * @param waypoints The list of waypoints for the trip.
     */
    fun updateSelectedPlaces(origin: SavedPlaceEntity, destination: SavedPlaceEntity, waypoints: List<SavedPlaceEntity>) {
        _selectedPlaces.value = listOf(origin, destination)
        _waypoints.value = waypoints
    }

    /**
     * Fetches routes for all travel modes (driving, walking, bicycling, transit) in parallel.
     *
     * Updates the `routesForModes` StateFlow with the results.
     *
     * @param origin The place ID of the origin.
     * @param destination The place ID of the destination.
     * @param waypoints The list of place IDs for waypoints (optional).
     */
    fun fetchRoutes(origin: String, destination: String, waypoints: List<String>) {
        viewModelScope.launch {
            _routesForModes.value = fetchRoutesForAllModes(origin, destination, waypoints)
        }
    }

    /**
     * Fetches routes for all modes in parallel using coroutines.
     *
     * @param origin The place ID of the origin.
     * @param destination The place ID of the destination.
     * @param waypoints The list of place IDs for waypoints (optional).
     * @return A map of travel modes to their corresponding routes.
     */
    private suspend fun fetchRoutesForAllModes(
        origin: String,
        destination: String,
        waypoints: List<String>?,
    ): Map<String, Pair<Route?, String?>> = coroutineScope {
        val modes = listOf("driving", "walking", "bicycling", "transit")

        val requests = modes.associateWith { mode ->
            async {
                try {
                    val route = fetchRoute(origin, destination, waypoints, mode)
                    route to null // Success: route with no error
                } catch (e: Exception) {
                    null to (e.message ?: "An unknown error occurred.") // Failure: no route with an error message
                }
            }
        }

        requests.mapValues { (_, deferred) -> deferred.await() }
    }

    /**
     * Fetches a single route for a specific travel mode.
     *
     * @param origin The place ID of the origin.
     * @param destination The place ID of the destination.
     * @param waypoints The list of place IDs for waypoints (optional).
     * @param mode The travel mode (e.g., "driving", "walking").
     * @return The Route object, or null if no route is available.
     */
    private suspend fun fetchRoute(
        origin: String,
        destination: String,
        waypoints: List<String>?,
        mode: String,
    ): Route? {
        val waypointsString = waypoints?.joinToString("|") { "place_id:$it" }?.let { "optimize:true|$it" }
        val response: DirectionsResponse = RetrofitInstance.directionsApi.getDirections(
            origin = "place_id:$origin",
            destination = "place_id:$destination",
            waypoints = waypointsString,
            mode = mode,
            key = API_KEY
        )

        return when (response.status) {
            "OK" -> response.routes.firstOrNull()
            "ZERO_RESULTS" -> throw Exception("No route found for mode: $mode.")
            "INVALID_REQUEST" -> throw Exception("$mode mode does not support waypoints.")
            else -> throw Exception("Error: ${response.status}")
        }
    }

    // Fetch planned trips from Firebase
    fun fetchPlannedTrips(userId: String) {
        val db = FirebaseFirestore.getInstance()
        db.collection("planned_trips")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { snapshot ->
                plannedTrips.clear()
                plannedTrips.addAll(snapshot.toObjects(PlannedTrip::class.java))
            }
            .addOnFailureListener { e ->
                Log.e("DirectionsViewModel", "Error fetching planned trips: ${e.message}", e)
            }
    }

    // Save a planned trip to Firebase
    fun savePlannedTrip(trip: PlannedTrip) {
        val db = FirebaseFirestore.getInstance()
        db.collection("planned_trips")
            .document(trip.tripId)
            .set(trip)
            .addOnSuccessListener {
                Log.d("DirectionsViewModel", "Planned trip saved successfully!")
            }
            .addOnFailureListener { e ->
                Log.e("DirectionsViewModel", "Error saving planned trip: ${e.message}", e)
            }
    }

}
