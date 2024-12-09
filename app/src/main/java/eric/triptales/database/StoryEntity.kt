package eric.triptales.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * Represents a story entity in the database.
 *
 * This data class defines the schema for the `stories` table. Each story is linked to a place
 * in the `places` table through a foreign key relationship. Stories are automatically deleted
 * when their associated place is removed from the database.
 *
 * @property story_id The unique identifier for the story. Automatically generated.
 * @property title The title of the story.
 * @property content The main content or description of the story.
 * @property created_at The timestamp indicating when the story was created.
 * @property place_id The unique ID of the associated place in the `places` table.
 */
@Entity(
    tableName = "stories",
    foreignKeys = [
        ForeignKey(
            entity = PlaceEntity::class,
            parentColumns = ["id"],
            childColumns = ["place_id"],
            onDelete = ForeignKey.CASCADE // Deletes stories when the associated place is removed
        )
    ]
)
data class StoryEntity(
    @PrimaryKey(autoGenerate = true) val story_id: Int = 0, // Auto-incremented primary key for each story
    val title: String, // The title of the story
    val content: String, // The content or description of the story
    val created_at: Long, // The creation timestamp for the story
    val place_id: String // Foreign key linking the story to a place
)
