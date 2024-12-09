package eric.triptales.firebase.entity

/**
 * Represents a community story in the Firebase database.
 *
 * This data class is used to model stories shared by users, including details about the story,
 * the user who created it, and the associated place. It also includes metadata like likes
 * and timestamps.
 *
 * @property id The unique identifier for the community story.
 * @property title The title of the story.
 * @property content The main content or description of the story.
 * @property created_at The timestamp when the story was created. Defaults to the current system time.
 * @property user_id The unique ID of the user who created the story.
 * @property username The username of the user who created the story.
 * @property place_id The unique ID of the associated place.
 * @property place_photos A list of photo references for the associated place (nullable).
 * @property place_name The name of the associated place (nullable).
 * @property place_address The address of the associated place (nullable).
 * @property likes The number of likes the story has received.
 */
data class CommunityStory(
    val id: String = "", // Unique identifier for the story
    val title: String = "", // Title of the story
    val content: String = "", // Content or description of the story
    val created_at: Long = System.currentTimeMillis(), // Timestamp for when the story was created
    val user_id: String = "", // ID of the user who created the story
    val username: String = "", // Username of the creator
    val place_id: String = "", // ID of the associated place
    val place_photos: List<String>? = listOf(), // List of photo references for the place
    val place_name: String? = "", // Name of the associated place
    val place_address: String? = "", // Address of the associated place
    val likes: Int = 0 // Number of likes the story has received
)
