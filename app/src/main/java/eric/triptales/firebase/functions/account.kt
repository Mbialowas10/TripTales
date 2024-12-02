package eric.triptales.firebase.functions

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

fun updateDisplayName(newDisplayName: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
    val user = FirebaseAuth.getInstance().currentUser

    if (user != null) {
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(newDisplayName)
            .build()

        user.updateProfile(profileUpdates)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess()
                } else {
                    onFailure(task.exception?.message ?: "Error updating display name")
                }
            }
    } else {
        onFailure("User not signed in")
    }
}
