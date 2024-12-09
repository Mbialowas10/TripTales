package eric.triptales.firebase.entity

/**
 * Represents a "like" on a story in the Firebase database.
 *
 * This data class models a user's like on a specific story, including the user who liked it,
 * the story that was liked, and the timestamp when the like was created.
 *
 * @property storyId The unique identifier of the story that was liked.
 * @property userId The unique identifier of the user who liked the story.
 * @property created_at The timestamp indicating when the like was created. Defaults to the current system time.
 */
data class StoryLikes(
    val storyId: String = "", // ID of the story that was liked
    val userId: String = "", // ID of the user who liked the story
    val created_at: Long = System.currentTimeMillis() // Timestamp when the like was created
)
