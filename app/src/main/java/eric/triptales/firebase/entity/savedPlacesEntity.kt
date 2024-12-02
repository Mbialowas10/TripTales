package eric.triptales.firebase.entity

data class SavedPlaceEntity(
    val placeId: String = "",
    val userId: String = "",
    val documentId: String = "",
    val name: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val rating: Double? = null,
    val address: String = "",
    val category: List<String>? = null,
    val formattedPhoneNumber: String? = null,
    val website: String? = null,
    val photos: List<String>? = null,
    val savedAt: Long = System.currentTimeMillis()
)
