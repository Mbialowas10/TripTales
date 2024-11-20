package eric.triptales.viewmodel

import android.app.Application
import androidx.compose.runtime.remember
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import eric.triptales.api.DirectionsApiService
import eric.triptales.api.DirectionsResponse
import eric.triptales.api.Route
import eric.triptales.api.RetrofitInstance
import eric.triptales.firebase.PlannedTrip
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class DirectionsViewModel(application: Application) : AndroidViewModel(application) {
    private val API_KEY = "AIzaSyBQtniS0NCgJc5D5g_t_ke42u5_ttYn4Rw"

    // StateFlow to store routes for all modes
    private val _routesForModes = MutableStateFlow<Map<String, Route?>>(emptyMap())
    val routesForModes: StateFlow<Map<String, Route?>> get() = _routesForModes

    // Reference to Directions API from RetrofitInstance
    private val directionsApi: DirectionsApiService = RetrofitInstance.directionsApi

    val plannedTrips = mutableListOf<PlannedTrip>()



    // Fetch routes for all modes: driving, walking, bicycling, and transit
    fun fetchRoutes(
        origin: String,
        destination: String,
        waypoints: List<String>
    ) {
        viewModelScope.launch {
            val routes = fetchRoutesForAllModes(origin, destination, waypoints)
            _routesForModes.value = routes
        }
    }

    // Perform parallel API calls for all modes
    private suspend fun fetchRoutesForAllModes(
        origin: String,
        destination: String,
        waypoints: List<String>?,
    ): Map<String, Route?> = coroutineScope {
        val modes = listOf("driving", "walking", "bicycling", "transit")

        // Launch parallel API requests for all modes
        val requests = modes.associateWith { mode ->
            async {
                fetchRoute(origin, destination, waypoints, mode)
            }
        }

        // Wait for all requests to complete and collect results
        requests.mapValues { (_, deferred) -> deferred.await() }
    }

    // Fetch a single route for the specified mode
    private suspend fun fetchRoute(
        origin: String,
        destination: String,
        waypoints: List<String>?,
        mode: String,
    ): Route? {
        val waypointsString = waypoints?.joinToString("|") { "place_id:$it" } ?: ""
        val response: DirectionsResponse = directionsApi.getDirections(
            origin = "place_id:$origin",
            destination = "place_id:$destination",
            waypoints = waypointsString,
            mode = mode,
            key = API_KEY
        )
        return response.routes.firstOrNull()
    }
}
