package eric.triptales.firebase.entity

data class PlannedTrip(
    val tripId: String = "",
    val userId: String = "",
    val name: String = "", // Name of the trip (e.g., "Weekend Getaway")
    val origin: SavedPlaceEntity = SavedPlaceEntity(), // Origin place
    val destination: SavedPlaceEntity = SavedPlaceEntity(), // Destination place
    val waypoints: List<SavedPlaceEntity> = emptyList(), // Waypoints
    val routeInfo: RouteInfo? = null, // Optional route details
    val createdAt: Long = System.currentTimeMillis() // Timestamp of creation
)

data class RouteInfo(
    val distance: String = "", // e.g., "150 km"
    val duration: String = "", // e.g., "2 hours 30 minutes"
    val polyline: String = ""
)