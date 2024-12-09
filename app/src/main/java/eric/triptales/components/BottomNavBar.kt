package eric.triptales.components

import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController

/**
 * Composable function for creating a bottom navigation bar.
 *
 * The bar contains multiple navigation items, each associated with a screen in the app.
 *
 * @param selectedScreen The currently selected screen's identifier.
 * @param navController The [NavController] used to handle navigation between screens.
 */
@Composable
fun BottomNavigationBar(selectedScreen: String, navController: NavController) {
    NavigationBar(
        containerColor = Color.LightGray,
        contentColor = Color.Black
    ) {
        // Home navigation item
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            selected = selectedScreen == "Home",
            onClick = { navController.navigate("home") }
        )
        // Search navigation item
        NavigationBarItem(
            icon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            selected = selectedScreen == "Search",
            onClick = { navController.navigate("search") }
        )
        // Planned Trip navigation item
        NavigationBarItem(
            icon = { Icon(Icons.Default.Create, contentDescription = "Planned Trip") },
            selected = selectedScreen == "Plan",
            onClick = { navController.navigate("plan") }
        )
        // Saved Places navigation item
        NavigationBarItem(
            icon = { Icon(Icons.Default.Favorite, contentDescription = "Saved Places") },
            selected = selectedScreen == "Saved",
            onClick = { navController.navigate("saved") }
        )
        // Account navigation item
        NavigationBarItem(
            icon = { Icon(Icons.Default.AccountCircle, contentDescription = "Account") },
            selected = selectedScreen == "Account",
            onClick = { navController.navigate("account") }
        )
    }
}
