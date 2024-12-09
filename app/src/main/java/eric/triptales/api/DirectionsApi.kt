package eric.triptales.api

import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Interface for accessing Google Directions API.
 * This interface defines endpoints for retrieving route directions.
 */
interface DirectionsApiService {

    /**
     * Fetches directions between two locations using the Google Directions API.
     *
     * @param origin The starting point for the route (latitude,longitude).
     * @param destination The ending point for the route (latitude,longitude).
     * @param key The API key for authenticating the request.
     * @return A [Call] object containing the [DirectionsResponse].
     */
    @GET("directions/json")
    suspend fun getDirections(
        @Query("origin") origin: String,
        @Query("destination") destination: String,
        @Query("waypoints") waypoints: String?,
        @Query("mode") mode: String,
        @Query("key") key: String
    ): DirectionsResponse
}
