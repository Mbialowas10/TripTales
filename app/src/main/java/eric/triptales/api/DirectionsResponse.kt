package eric.triptales.api

data class DirectionsResponse(
    val routes: List<Route>,
    val status: String
)

data class Route(
    val overview_polyline: OverviewPolyline,
    val legs: List<Leg>
)

data class OverviewPolyline(val points: String)

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
