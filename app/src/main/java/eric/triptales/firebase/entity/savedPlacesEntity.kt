package eric.triptales.firebase.entity

/**
 * Represents a saved place entity in the Firebase database.
 *
 * This data class is used to store information about a place saved by a user,
 * including its details, associated user, and metadata like the save timestamp.
 *
 * @property placeId The unique identifier for the place.
 * @property userId The unique identifier of the user who saved the place.
 * @property documentId The unique identifier for the document in Firebase.
 * @property name The name of the place.
 * @property latitude The latitude coordinate of the place.
 * @property longitude The longitude coordinate of the place.
 * @property rating The average rating of the place (nullable).
 * @property address The address of the place.
 * @property category A list of categories or tags associated with the place (nullable).
 * @property formattedPhoneNumber The phone number of the place (nullable).
 * @property website The website URL of the place (nullable).
 * @property photos A list of photo references associated with the place (nullable).
 * @property savedAt The timestamp indicating when the place was saved. Defaults to the current system time.
 */
data class SavedPlaceEntity(
    val placeId: String = "", // Unique ID for the place
    val userId: String = "", // ID of the user who saved the place
    val documentId: String = "", // Unique document ID in Firebase
    val name: String = "", // Name of the place
    val latitude: Double = 0.0, // Latitude coordinate
    val longitude: Double = 0.0, // Longitude coordinate
    val rating: Double? = null, // Average rating (optional)
    val address: String = "", // Address of the place
    val category: List<String>? = null, // Categories or tags (optional)
    val formattedPhoneNumber: String? = null, // Phone number (optional)
    val website: String? = null, // Website URL (optional)
    val photos: List<String>? = null, // List of photo references (optional)
    val savedAt: Long = System.currentTimeMillis() // Timestamp when the place was saved
)
