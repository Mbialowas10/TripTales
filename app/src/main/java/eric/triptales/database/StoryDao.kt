package eric.triptales.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * Data Access Object (DAO) for managing database operations related to stories.
 *
 * This interface provides methods for inserting, retrieving, and deleting story entities
 * stored in the database. It uses Room annotations to map SQL queries to Kotlin functions.
 */
@Dao
interface StoryDao {

    /**
     * Inserts a new story into the database.
     *
     * If a conflict occurs (e.g., a story with the same ID exists), it will be ignored
     * unless otherwise specified by the `OnConflictStrategy`.
     *
     * @param story The [StoryEntity] object to insert.
     */
    @Insert
    suspend fun insertStory(story: StoryEntity)

    /**
     * Retrieves a list of stories associated with a specific place ID.
     *
     * @param placeId The unique ID of the place for which stories are to be fetched.
     * @return A list of [StoryEntity] objects linked to the specified place ID.
     */
    @Query("SELECT * FROM stories WHERE place_id = :placeId")
    fun getStoriesForPlace(placeId: String): List<StoryEntity>

    /**
     * Deletes a story from the database by its ID.
     *
     * @param id The unique ID of the story to delete.
     */
    @Query("DELETE FROM stories WHERE story_id = :id")
    suspend fun deleteStory(id: Int)
}
