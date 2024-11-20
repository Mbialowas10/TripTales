package eric.triptales.api

import retrofit2.http.GET
import retrofit2.http.Query

interface PlacesApi {
    @GET("place/nearbysearch/json")
    suspend fun getNearbyAttractions(
        @Query("location") location: String,
        @Query("radius") radius: Int,
        @Query("type") type: String = "tourist_attraction",
        @Query("key") apiKey: String
    ): PlacesResponse

    @GET("place/autocomplete/json")
    suspend fun getAutocompleteResults(
        @Query("input") input: String,
        @Query("types") types: String,
        @Query("key") apiKey: String
    ): AutocompleteResponse

    @GET("place/details/json")
    suspend fun getPlaceDetails(
        @Query("place_id") placeId: String,
        @Query("key") apiKey: String
    ): PlaceDetailResponse
}

