package eric.triptales.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import eric.triptales.components.BottomNavigationBar
import eric.triptales.firebase.functions.updateDisplayName

@Composable
fun AccountScreen(navController: NavController) {
    var displayName by remember { mutableStateOf(TextFieldValue("")) }
    var isSaving by remember { mutableStateOf(false) }

    // Initialize with current display name
    LaunchedEffect(Unit) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        displayName = TextFieldValue(currentUser?.displayName ?: "")
    }

    Scaffold(
        topBar = { eric.triptales.components.TopAppBar("Account Setting", "main", navController) },
        bottomBar = { BottomNavigationBar("Account", navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "User Settings", style = MaterialTheme.typography.headlineMedium)

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = displayName,
                onValueChange = { displayName = it },
                label = { Text("Display Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    isSaving = true
                    updateDisplayName(
                        newDisplayName = displayName.text,
                        onSuccess = {
                            isSaving = false
                        },
                        onFailure = {
                            isSaving = false
                        }
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isSaving
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text("Save Changes")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}