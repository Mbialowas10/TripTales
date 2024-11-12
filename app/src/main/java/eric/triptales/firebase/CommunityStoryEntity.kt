package eric.triptales.firebase

data class CommunityStory(
    val id: String = "",
    val title: String = "",
    val content: String = "",
    val created_at: Long = System.currentTimeMillis(),
    val user_id: String = "",
    val username: String = "",
    val place_id: String = "",
    val likes: Int = 0
)