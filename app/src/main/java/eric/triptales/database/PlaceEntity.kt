package eric.triptales.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "places")
data class PlaceEntity(
    @PrimaryKey val id: String,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val rating: Double?,
    val address: String,
    val category: List<String>?,
    val formatted_phone_number: String?,
    val website: String?,
    val saved_at: Long
)
