package eric.triptales.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.google.android.libraries.places.api.model.Place

@Dao
interface PlaceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(places: List<PlaceEntity>)

    @Query("DELETE FROM places WHERE id = :placeId")
    suspend fun deleteById(placeId: String)


    @Query("SELECT * FROM places WHERE id = :id")
    suspend fun getPlace(id: String): PlaceEntity

    @Query("SELECT * FROM places WHERE latitude = :lat AND longitude = :lng")
    suspend fun getPlacesByLocation(lat: Double, lng: Double): List<PlaceEntity>

    @Query("SELECT * FROM places")
    suspend fun getAllPlaces(): List<PlaceEntity>
}