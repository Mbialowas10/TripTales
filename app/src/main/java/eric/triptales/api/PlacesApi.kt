package eric.triptales.api

import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Interface for accessing the Google Places API.
 * This interface defines endpoints for retrieving place details and nearby attractions.
 */
interface PlacesApi {
    /**
     * Fetches a list of nearby places using the Google Places API.
     *
     * @param location The location to search around, specified as latitude,longitude.
     * @param radius The radius (in meters) around the location to search within.
     * @param type The type of places to search for (e.g., "restaurant", "hotel").
     * @param key The API key for authenticating the request.
     * @return A [Call] object containing the [PlacesResponse].
     */
    @GET("place/nearbysearch/json")
    suspend fun getNearbyAttractions(
        @Query("location") location: String,
        @Query("radius") radius: Int,
        @Query("type") type: String = "tourist_attraction",
        @Query("key") apiKey: String
    ): PlacesResponse

    /**
     * Fetches autocomplete predictions for a given input using the Google Places API.
     *
     * This endpoint is typically used for providing suggestions as the user types in a search box.
     *
     * @param input The text input for which predictions are requested.
     * @param types The type of predictions to restrict the results to (e.g., "geocode", "establishment").
     * @param apiKey The API key for authenticating the request.
     * @return A [AutocompleteResponse] object containing the autocomplete results.
     */
    @GET("place/autocomplete/json")
    suspend fun getAutocompleteResults(
        @Query("input") input: String,
        @Query("types") types: String,
        @Query("key") apiKey: String
    ): AutocompleteResponse

    /**
     * Fetches details of a specific place using the Google Places API.
     *
     * @param placeId The unique ID of the place to retrieve details for.
     * @param key The API key for authenticating the request.
     * @return A [Call] object containing the [PlacesResponse].
     */
    @GET("place/details/json")
    suspend fun getPlaceDetails(
        @Query("place_id") placeId: String,
        @Query("key") apiKey: String
    ): PlaceDetailResponse
}

