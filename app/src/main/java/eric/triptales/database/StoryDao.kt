package eric.triptales.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface StoryDao {
    @Insert
    suspend fun insertStory(story: StoryEntity)

    @Query("SELECT * FROM stories WHERE place_id = :placeId")
    fun getStoriesForPlace(placeId: String): List<StoryEntity>

    @Query("DELETE FROM stories WHERE story_id = :id")
    suspend fun deleteStory(id: Int)
}

