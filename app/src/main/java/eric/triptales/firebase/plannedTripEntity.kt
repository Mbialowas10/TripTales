package eric.triptales.firebase

data class PlannedTrip(
    val tripName: String,           // Trip name
    val places: List<Place>,
    val routeInfo: RouteInfo? = null
)

data class Place(
    val name: String,       // Place name
    val place_id: String,
    val type: String        // "Origin", "Waypoint", or "Destination"
)

data class RouteInfo(
    val totalDistance: String,
    val totalDuration: String,
    val polyline: String        // Encoded polyline for the route
)
