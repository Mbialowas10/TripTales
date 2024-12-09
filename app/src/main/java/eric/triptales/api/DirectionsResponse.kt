package eric.triptales.api

/**
 * Data class representing the response from the Google Directions API.
 *
 * This class contains all the fields returned by the API, such as routes, waypoints, and status.
 */
data class DirectionsResponse(
    val routes: List<Route>,
    val status: String
)

/**
 * Data class representing a single route in the response.
 *
 * Each route contains legs and an overview of the polyline.
 */
data class Route(
    val overview_polyline: OverviewPolyline,
    val legs: List<Leg>
)

/**
 * Overview of the polyline for the entire route.
 */
data class OverviewPolyline(val points: String)

/**
 * Data class representing a leg of the route.
 *
 * Each leg contains details about the segment of the journey, such as distance and duration.
 */
data class Leg(
    val distance: Distance,
    val duration: Duration,
    val start_address: String,
    val end_address: String,
    val steps: List<Step>
)

data class Step (
    val distance: Distance,
    val duration: Duration,
    val html_instructions: String,
    val start_location: LatLngLiteral,
    val end_location: LatLngLiteral,
    val travel_mode: String,
)

data class LatLngLiteral(
    val lat: Double,
    val lng: Double
)

data class Distance(val text: String, val value: Int) // Value in meters
data class Duration(val text: String, val value: Int) // Value in seconds
