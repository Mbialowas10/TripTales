package eric.triptales.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.FieldValue


fun postStoryToCommunity(story: CommunityStory, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    db.collection("community_stories")
        .add(story)
        .addOnSuccessListener { onSuccess() }
        .addOnFailureListener { exception ->
            onFailure(exception.message ?: "Error posting story")
        }
}

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


fun likeCommunityStory(storyId: String, userId: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    val storyRef = db.collection("community_stories").document(storyId)
    val storyLikeEntry = StoryLikes(storyId, userId)

    storyRef.update("likes", FieldValue.increment(1))
        .addOnSuccessListener { onSuccess() }
        .addOnFailureListener { exception ->
            onFailure(exception.message ?: "Error liking story")
        }

    db.collection("stories_likes")
        .add(storyLikeEntry)
        .addOnFailureListener { exception ->
            onFailure(exception.message ?: "Error liking story")
        }
}
