package eric.triptales.firebase.entity

data class StoryLikes(
    val storyId: String = "",
    val userId: String = "",
    val created_at: Long = System.currentTimeMillis(),
)