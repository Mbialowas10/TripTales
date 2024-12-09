package eric.triptales.components

import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController

/**
 * A composable function that displays a customizable top app bar.
 *
 * The `TopAppBar` supports two types:
 * - "main": Displays a placeholder for a menu button (currently commented out).
 * - "sub": Displays a back button that navigates to the previous screen.
 *
 * @param title The title to display in the app bar.
 * @param type A [String] indicating the type of the app bar ("main" or "sub").
 * @param navController The [NavController] used for navigation actions.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(title: String, type: String, navController: NavController) {
    TopAppBar(
        title = {
            Text(text = title) // Displays the app bar title
        },
        navigationIcon = {
            when (type) {
                "main" -> {
                    // Uncomment and implement if a menu button is needed
                    // IconButton(onClick = { /* Handle menu click */ }) {
                    //     Icon(Icons.Default.Menu, contentDescription = "Menu")
                    // }
                }
                "sub" -> {
                    /**
                     * Displays a back button for "sub" type that navigates back in the stack.
                     */
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "Back"
                        )
                    }
                }
            }
        },
        colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(
            containerColor = Color.LightGray, // Background color of the app bar
            titleContentColor = Color.Black  // Color of the title text
        )
    )
}
