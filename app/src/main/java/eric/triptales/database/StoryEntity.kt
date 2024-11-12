package eric.triptales.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "stories",
    foreignKeys = [
        ForeignKey(
            entity = PlaceEntity::class,
            parentColumns = ["id"],
            childColumns = ["place_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class StoryEntity(
    @PrimaryKey(autoGenerate = true) val story_id: Int = 0,
    val title: String,
    val content: String,
    val created_at: Long,
    val place_id: String
)
