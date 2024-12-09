package eric.triptales.firebase.functions

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

/**
 * Updates the display name of the currently authenticated user in Firebase.
 *
 * This function checks if a user is signed in and updates their display name using
 * Firebase Authentication. It provides callbacks for success and failure scenarios.
 *
 * @param newDisplayName The new display name to set for the user.
 * @param onSuccess A callback function invoked when the update is successful.
 * @param onFailure A callback function invoked when the update fails, with an error message.
 */
fun updateDisplayName(
    newDisplayName: String,
    onSuccess: () -> Unit,
    onFailure: (String) -> Unit
) {
    // Get the current authenticated user
    val user = FirebaseAuth.getInstance().currentUser

    if (user != null) {
        // Build the request to update the display name
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(newDisplayName)
            .build()

        // Perform the update and handle the result
        user.updateProfile(profileUpdates)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Invoke the success callback
                    onSuccess()
                } else {
                    // Invoke the failure callback with the error message
                    onFailure(task.exception?.message ?: "Error updating display name")
                }
            }
    } else {
        // Invoke the failure callback if no user is signed in
        onFailure("User not signed in")
    }
}
