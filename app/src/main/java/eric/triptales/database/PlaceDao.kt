package eric.triptales.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * Data Access Object (DAO) for managing database operations related to places.
 *
 * This interface defines methods to insert, delete, and query place entities
 * stored in the database. It uses Room annotations to map SQL queries to Kotlin functions.
 */
@Dao
interface PlaceDao {

    /**
     * Inserts a list of places into the database.
     *
     * If a conflict occurs (e.g., a place with the same ID already exists),
     * the existing entry is replaced with the new data.
     *
     * @param places The list of [PlaceEntity] objects to insert.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(places: List<PlaceEntity>)

    /**
     * Deletes a place from the database by its ID.
     *
     * @param placeId The unique ID of the place to delete.
     */
    @Query("DELETE FROM places WHERE id = :placeId")
    suspend fun deleteById(placeId: String)

    /**
     * Retrieves a place entity by its ID.
     *
     * @param id The unique ID of the place to retrieve.
     * @return The [PlaceEntity] object corresponding to the given ID.
     */
    @Query("SELECT * FROM places WHERE id = :id")
    suspend fun getPlace(id: String): PlaceEntity

    /**
     * Retrieves all places from the database.
     *
     * @return A list of all [PlaceEntity] objects stored in the database.
     */
    @Query("SELECT * FROM places")
    suspend fun getAllPlaces(): List<PlaceEntity>
}
