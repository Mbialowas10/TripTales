package eric.triptales.firebase

data class StoryLikes(
    val storyId: String = "",
    val userId: String = "",
    val created_at: Long = System.currentTimeMillis(),
)