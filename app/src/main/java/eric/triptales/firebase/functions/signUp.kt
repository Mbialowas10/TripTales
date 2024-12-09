package eric.triptales.firebase.functions

import com.google.firebase.auth.FirebaseAuth

/**
 * Registers a new user with an email and password using Firebase Authentication.
 *
 * This function creates a new user account in Firebase with the provided email and password.
 * It invokes success and error callbacks based on the result of the registration process.
 *
 * @param email The email address for the new user account.
 * @param password The password for the new user account.
 * @param onSignUpSuccess A callback invoked when the registration is successful.
 * @param onError A callback invoked with an error message if the registration fails.
 */
fun signUpWithEmailPassword(
    email: String,
    password: String,
    onSignUpSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Invoke success callback if the registration is successful
                onSignUpSuccess()
            } else {
                // Invoke error callback with an error message
                onError(task.exception?.message ?: "Registration failed")
            }
        }
}
