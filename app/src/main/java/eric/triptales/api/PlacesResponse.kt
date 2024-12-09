package eric.triptales.api

/**
 * Represents the response from the Google Places API for nearby places or searches.
 *
 * @property results The list of places matching the query.
 * @property status The status of the request (e.g., "OK", "ZERO_RESULTS").
 */
data class PlacesResponse(
    val results: List<PlaceResult>,
    val status: String
)

/**
 * Represents a single place result in the Google Places API response.
 *
 * @property name The name of the place.
 * @property place_id The unique identifier of the place.
 * @property geometry The geographical information of the place.
 * @property rating The rating of the place, if available.
 * @property vicinity A brief address or location description of the place.
 */
data class PlaceResult(
    val name: String,
    val place_id: String,
    val geometry: Geometry,
    val rating: Double?,
    val vicinity: String
)

/**
 * Represents the detailed response from the Google Places API for a specific place.
 *
 * @property result The detailed information about the place.
 * @property status The status of the request (e.g., "OK", "NOT_FOUND").
 */
data class PlaceDetailResponse(
    val result: PlaceDetailResult,
    val status: String
)

/**
 * Represents detailed information about a specific place.
 *
 * @property place_id The unique identifier of the place.
 * @property name The name of the place.
 * @property geometry The geographical information of the place.
 * @property rating The rating of the place, if available.
 * @property formatted_address The formatted address of the place.
 * @property types The types/categories of the place.
 * @property formatted_phone_number The phone number of the place, if available.
 * @property website The website of the place, if available.
 * @property photos The list of photos related to the place.
 * @property reviews The list of reviews for the place.
 */
data class PlaceDetailResult(
    val place_id: String,
    val name: String,
    val geometry: Geometry,
    val rating: Double?,
    val formatted_address: String?,
    val types: List<String>?,
    val formatted_phone_number: String?,
    val website: String?,
    val photos: List<Photo>?,
    val reviews: List<Review>?
)

/**
 * Represents a review for a place.
 *
 * @property author_name The name of the author who wrote the review.
 * @property profile_photo_url The URL to the author's profile photo.
 * @property rating The rating given in the review.
 * @property text The content of the review.
 * @property relative_time_description A description of how recent the review was.
 */
data class Review(
    val author_name: String,
    val profile_photo_url: String,
    val rating: Double,
    val text: String,
    val relative_time_description: String
)

/**
 * Represents a photo associated with a place.
 *
 * @property height The height of the photo in pixels.
 * @property width The width of the photo in pixels.
 * @property photo_reference The reference string to fetch the photo.
 */
data class Photo(
    val height: Number,
    val width: Number,
    val photo_reference: String,
)

/**
 * Represents the geographical data of a place.
 *
 * @property location The latitude and longitude of the place.
 */
data class Geometry(
    val location: Location
)

/**
 * Represents the latitude and longitude of a place.
 *
 * @property lat The latitude of the place.
 * @property lng The longitude of the place.
 */
data class Location(
    val lat: Double,
    val lng: Double
)

/**
 * Represents the response from the autocomplete API.
 *
 * @property predictions The list of predictions matching the input query.
 */
data class AutocompleteResponse(
    val predictions: List<Prediction>
)

/**
 * Represents a single prediction in the autocomplete API response.
 *
 * @property description The description of the place prediction.
 * @property place_id The unique identifier of the predicted place.
 */
data class Prediction(
    val description: String,
    val place_id: String
)