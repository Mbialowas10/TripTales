package eric.triptales.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter

/**
 * Represents a place entity in the database.
 *
 * This data class defines the schema for the `places` table. Each place is uniquely identified
 * by its `id` and contains various attributes such as its name, location, rating, and more.
 *
 * @property id The unique identifier for the place.
 * @property name The name of the place.
 * @property latitude The latitude of the place's location.
 * @property longitude The longitude of the place's location.
 * @property rating The average rating of the place (nullable).
 * @property address The address of the place.
 * @property category A list of categories or tags associated with the place (nullable).
 * @property formatted_phone_number The phone number of the place (nullable).
 * @property website The website URL of the place (nullable).
 * @property photos A list of photo references associated with the place (nullable).
 * @property is_saved Indicates whether the place is saved by the user.
 * @property saved_at A timestamp of when the place was saved.
 */
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
    val photos: List<String>?,
    val is_saved: Boolean,
    val saved_at: Long
)

/**
 * A set of type converters to handle custom data types in the Room database.
 *
 * The [Converters] class provides methods to convert between unsupported types (e.g., lists)
 * and supported types (e.g., strings) for storing in the database. These converters are applied
 * using the `@TypeConverters` annotation in the database class.
 */
class Converters {

    /**
     * Converts a list of strings into a single comma-separated string for database storage.
     *
     * @param value The list of strings to convert.
     * @return A single string with list elements joined by commas, or an empty string if the list is null.
     */
    @TypeConverter
    fun fromStringList(value: List<String>?): String {
        return value?.joinToString(",") ?: ""
    }

    /**
     * Converts a comma-separated string back into a list of strings.
     *
     * @param value The comma-separated string to convert.
     * @return A list of strings obtained by splitting the input string, trimming whitespace around elements.
     */
    @TypeConverter
    fun toStringList(value: String): List<String> {
        return value.split(",").map { it.trim() }
    }
}
