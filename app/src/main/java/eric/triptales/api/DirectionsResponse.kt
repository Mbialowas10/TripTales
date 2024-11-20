package eric.triptales.api

data class DirectionsResponse(
    val routes: List<Route>
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
    val end_address: String
)

data class Distance(val text: String, val value: Int) // Value in meters
data class Duration(val text: String, val value: Int) // Value in seconds
