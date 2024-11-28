package eric.triptales.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import eric.triptales.api.DirectionsApiService
import eric.triptales.api.DirectionsResponse
import eric.triptales.api.Route
import eric.triptales.api.RetrofitInstance
import eric.triptales.firebase.PlannedTrip
import eric.triptales.firebase.SavedPlaceEntity
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

    // StateFlow to hold the routes for all modes, exposed to the UI
    private val _routesForModes = MutableStateFlow<Map<String, Route?>>(emptyMap())
    val routesForModes: StateFlow<Map<String, Route?>> get() = _routesForModes

    // Reference to the Directions API service for making network requests
    private val directionsApi: DirectionsApiService = RetrofitInstance.directionsApi

    // List to hold planned trips (local in-memory storage)
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
            // Perform parallel API calls to fetch routes for all modes
            val routes = fetchRoutesForAllModes(origin, destination, waypoints)
            _routesForModes.value = routes
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
    ): Map<String, Route?> = coroutineScope {
        // List of supported travel modes
        val modes = listOf("driving", "walking", "bicycling", "transit")

        // Launch parallel API requests for each mode
        val requests = modes.associateWith { mode ->
            async {
                fetchRoute(origin, destination, waypoints, mode)
            }
        }

        // Await all requests and collect results into a map
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
        // Convert waypoint list to a formatted string for the API
        val waypointsString = waypoints?.joinToString("|") { "place_id:$it" } ?: ""

        // Make API request to fetch directions
        val response: DirectionsResponse = directionsApi.getDirections(
            origin = "place_id:$origin",
            destination = "place_id:$destination",
            waypoints = waypointsString,
            mode = mode,
            key = API_KEY
        )

        // Return the first route from the response, if available
        return response.routes.firstOrNull()
    }
}
