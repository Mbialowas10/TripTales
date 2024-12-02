package eric.triptales.firebase.functions

import com.google.firebase.auth.FirebaseAuth

fun signInWithEmailPassword(
    email: String,
    password: String,
    onLoginSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onLoginSuccess()
            } else {
                onError(task.exception?.message ?: "Login failed")
            }
        }
}

