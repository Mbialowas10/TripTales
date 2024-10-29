package eric.triptales.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.material3.*
import androidx.navigation.NavController
import eric.triptales.components.TopAppBar
import eric.triptales.components.BottomNavigationBar

@Composable
fun HomeScreen(navController: NavController) {
    Scaffold(
        topBar = { TopAppBar("Home", "main", navController) },
        bottomBar = { BottomNavigationBar("Home", navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Text(
                text = "Saved Places",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(16.dp)
            )
            // Placeholder for Saved Places content
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Gray) // Placeholder color for saved places list
            ) {
                // You will replace this with your actual saved places list or LazyColumn
            }
        }
    }
}

