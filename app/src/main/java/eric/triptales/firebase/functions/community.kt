package eric.triptales.firebase.functions

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.FieldValue
import eric.triptales.firebase.entity.CommunityStory
import eric.triptales.firebase.entity.SavedPlaceEntity
import eric.triptales.firebase.entity.StoryLikes

/**
 * Posts a story to the "community_stories" collection in Firebase.
 *
 * @param story The [CommunityStory] object to post.
 * @param onSuccess A callback invoked when the operation is successful.
 * @param onFailure A callback invoked when the operation fails, with an error message.
 */
fun postStoryToCommunity(story: CommunityStory, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    db.collection("community_stories")
        .add(story)
        .addOnSuccessListener { onSuccess() }
        .addOnFailureListener { exception ->
            onFailure(exception.message ?: "Error posting story")
        }
}

/**
 * Fetches all community stories from Firebase, sorted by creation time in descending order.
 *
 * @param onResult A callback invoked with the list of [CommunityStory] objects when successful.
 * @param onError A callback invoked with an error message when the operation fails.
 */
fun getCommunityStories(onResult: (List<CommunityStory>) -> Unit, onError: (String) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    db.collection("community_stories")
        .orderBy("created_at", Query.Direction.DESCENDING)
        .get()
        .addOnSuccessListener { documents ->
            val stories = documents.map { document ->
                val story = document.toObject(CommunityStory::class.java).copy(id = document.id)
                story
            }
            onResult(stories)
        }
        .addOnFailureListener { exception ->
            onError(exception.message ?: "Error fetching stories")
        }
}

/**
 * Likes a community story by incrementing the "likes" field and adding an entry in "stories_likes".
 *
 * @param storyId The ID of the story to like.
 * @param userId The ID of the user liking the story.
 * @param onSuccess A callback invoked when the operation is successful.
 * @param onFailure A callback invoked when the operation fails, with an error message.
 */
fun likeCommunityStory(storyId: String, userId: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    val storyRef = db.collection("community_stories").document(storyId)
    val storyLikeEntry = StoryLikes(storyId, userId)

    // Increment the likes count on the story document
    storyRef.update("likes", FieldValue.increment(1))
        .addOnSuccessListener { onSuccess() }
        .addOnFailureListener { exception ->
            onFailure(exception.message ?: "Error liking story")
        }

    // Add the like entry to the "stories_likes" collection
    db.collection("stories_likes")
        .add(storyLikeEntry)
        .addOnFailureListener { exception ->
            onFailure(exception.message ?: "Error liking story")
        }
}

/**
 * Fetches all saved places for a specific user from the "saved_places" collection.
 *
 * @param userId The ID of the user whose saved places are being fetched.
 * @param onSuccess A callback invoked with the list of [SavedPlaceEntity] objects when successful.
 * @param onFailure A callback invoked with an error message when the operation fails.
 */
fun fetchSavedStories(userId: String, onSuccess: (List<SavedPlaceEntity>) -> Unit, onFailure: (String) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    db.collection("saved_places")
        .whereEqualTo("userId", userId)
        .get()
        .addOnSuccessListener { documents ->
            val places = documents.map { doc ->
                SavedPlaceEntity(
                    placeId = doc.getString("placeId") ?: "",
                    userId = doc.getString("userId") ?: "",
                    documentId = doc.id, // Firebase document ID
                    name = doc.getString("name") ?: "",
                    latitude = doc.getDouble("latitude") ?: 0.0,
                    longitude = doc.getDouble("longitude") ?: 0.0,
                    rating = doc.getDouble("rating"),
                    address = doc.getString("address") ?: "",
                    category = doc.get("category") as? List<String>,
                    formattedPhoneNumber = doc.getString("formattedPhoneNumber"),
                    website = doc.getString("website"),
                    photos = doc.get("photos") as? List<String>,
                    savedAt = doc.getLong("savedAt") ?: System.currentTimeMillis()
                )
            }
            onSuccess(places)
        }
        .addOnFailureListener { exception ->
            onFailure(exception.message ?: "Error fetching saved places")
        }
}
