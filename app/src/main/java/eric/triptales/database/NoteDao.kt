package eric.triptales.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface NoteDao {

    // Insert a new note
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: NoteEntity)

    // Query to get all notes for a specific place
    @Query("SELECT * FROM notes WHERE place_id = :placeId")
    suspend fun getNotesForPlace(placeId: String): List<NoteEntity>

    // Delete a note
    @Delete
    suspend fun deleteNote(note: NoteEntity)
}
