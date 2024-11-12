package eric.triptales.firebase

import com.google.firebase.auth.FirebaseAuth

fun signUpWithEmailPassword(
    email: String,
    password: String,
    onSignUpSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onSignUpSuccess()
            } else {
                onError(task.exception?.message ?: "Registration failed")
            }
        }
}
