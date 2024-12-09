package eric.triptales.firebase.entity

/**
 * Represents a planned trip in the application.
 *
 * This data class models a trip planned by a user, including the origin, destination, waypoints,
 * and optional route information. It also stores metadata like the trip name and creation timestamp.
 *
 * @property tripId The unique identifier for the trip.
 * @property userId The unique identifier of the user who created the trip.
 * @property name The name of the trip (e.g., "Weekend Getaway").
 * @property origin The starting location of the trip, represented by a [SavedPlaceEntity].
 * @property destination The ending location of the trip, represented by a [SavedPlaceEntity].
 * @property waypoints A list of intermediate stops along the trip, represented as [SavedPlaceEntity].
 * @property routeInfo Optional route details such as distance, duration, and polyline data.
 * @property createdAt The timestamp of when the trip was created. Defaults to the current system time.
 */
data class PlannedTrip(
    val tripId: String = "", // Unique ID for the trip
    val userId: String = "", // ID of the user who created the trip
    val name: String = "", // Name of the trip
    val origin: SavedPlaceEntity = SavedPlaceEntity(), // Origin location
    val destination: SavedPlaceEntity = SavedPlaceEntity(), // Destination location
    val waypoints: List<SavedPlaceEntity> = emptyList(), // List of waypoints
    val routeInfo: RouteInfo? = null, // Optional route information
    val createdAt: Long = System.currentTimeMillis() // Creation timestamp
)

/**
 * Represents detailed route information for a planned trip.
 *
 * This data class provides details about the route, including the total distance, duration,
 * and polyline data for mapping.
 *
 * @property distance The total distance of the route (e.g., "150 km").
 * @property duration The total duration of the route (e.g., "2 hours 30 minutes").
 * @property polyline The encoded polyline representing the route on a map.
 */
data class RouteInfo(
    val distance: String = "", // Distance of the route
    val duration: String = "", // Duration of the route
    val polyline: String = "" // Encoded polyline for the route
)
