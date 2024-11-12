package eric.triptales.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import eric.triptales.firebase.signInWithEmailPassword
import eric.triptales.firebase.signUpWithEmailPassword

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Login", style = MaterialTheme.typography.headlineLarge)

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            isError = errorMessage != null && email.isEmpty()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            isError = errorMessage != null && password.isEmpty()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                // Input validation
                if (email.isBlank() || password.isBlank()) {
                    errorMessage = "Please fill in both email and password."
                    isLoading = false
                } else {
                    isLoading = true
                    signInWithEmailPassword(email, password, onLoginSuccess = onLoginSuccess, onError = {
                        isLoading = false
                        errorMessage = it
                    })
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Login")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                // Input validation
                if (email.isBlank() || password.isBlank()) {
                    errorMessage = "Please fill in both email and password."
                    isLoading = false
                } else {
                    isLoading = true
                    signUpWithEmailPassword(email, password, onSignUpSuccess = onLoginSuccess, onError = {
                        isLoading = false
                        errorMessage = it
                    })
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Register")
        }

        // Display error message if there's any
        if (errorMessage != null) {
            Text(
                text = errorMessage!!,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        // Display loading indicator
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
        }
    }
}
