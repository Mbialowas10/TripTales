package eric.triptales.firebase.functions

import com.google.firebase.auth.FirebaseAuth

/**
 * Signs in a user with their email and password using Firebase Authentication.
 *
 * This function attempts to authenticate a user with the provided email and password.
 * It invokes success and error callbacks based on the result of the authentication process.
 *
 * @param email The email address of the user attempting to sign in.
 * @param password The password associated with the user's account.
 * @param onLoginSuccess A callback invoked when the login is successful.
 * @param onError A callback invoked with an error message if the login fails.
 */
fun signInWithEmailPassword(
    email: String,
    password: String,
    onLoginSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Invoke success callback if the authentication is successful
                onLoginSuccess()
            } else {
                // Invoke error callback with an error message
                onError(task.exception?.message ?: "Login failed")
            }
        }
}
