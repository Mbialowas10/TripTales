package eric.triptales.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PlaceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(places: List<PlaceEntity>)

    @Query("SELECT * FROM places WHERE latitude = :lat AND longitude = :lng")
    suspend fun getPlacesByLocation(lat: Double, lng: Double): List<PlaceEntity>
}