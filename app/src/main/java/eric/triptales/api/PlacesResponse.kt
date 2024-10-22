package eric.triptales.api

data class PlacesResponse(
    val results: List<PlaceResult>,
    val status: String
)

data class PlaceResult(
    val name: String,
    val placeId: String,
    val geometry: Geometry,
    val rating: Double?,
    val vicinity: String
)

data class PlaceDetailResponse(
    val result: PlaceDetailResult,
    val status: String
)

data class PlaceDetailResult(
    val place_id: String,
    val name: String,
    val geometry: Geometry,
    val rating: Double?,
    val formatted_address: String?,
    val types: List<String>?,
    val formatted_phone_number: String?,
    val website: String?,
)
data class Geometry(
    val location: Location
)

data class Location(
    val lat: Double,
    val lng: Double
)

data class AutocompleteResponse(
    val predictions: List<Prediction>
)

data class Prediction(
    val description: String,
    val place_id: String
)