package eric.triptales.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import eric.triptales.components.BottomNavigationBar
import eric.triptales.components.ListOfPlaces
import eric.triptales.components.SearchBar
import eric.triptales.components.TopAppBar
import eric.triptales.viewmodel.PlacesViewModel
import eric.triptales.components.MapScreen

/**
 * Displays the NearBy Search Screen, allowing users to search for nearby places and view a map.
 *
 * This screen includes a top app bar, bottom navigation bar, and content that dynamically switches
 * between a map view and other UI based on the `isFetchNearBy` state from the [PlacesViewModel].
 *
 * @param navController The [NavController] used for navigation between screens.
 * @param placesViewModel The [PlacesViewModel] to handle place-related data and operations.
 */
@Composable
fun NearBySearchScreen(navController: NavController, placesViewModel: PlacesViewModel) {
    Scaffold(
        topBar = {
            // Top app bar with title and back navigation
            TopAppBar(
                title = "Search near by",
                type = "sub",
                navController = navController
            )
        },
        bottomBar = {
            // Bottom navigation bar with "Search" selected
            BottomNavigationBar(selectedScreen = "Search", navController = navController)
        }
    ) { paddingValues ->
        // Main content area
        Column(
            modifier = Modifier
                .fillMaxSize() // Fill the available space
                .padding(paddingValues) // Apply scaffold padding
        ) {
            // Display the map screen if nearby places are not being fetched
            if (!placesViewModel.isFetchNearBy.value) {
                MapScreen(
                    placesViewModel = placesViewModel,
                    navController = navController
                )
            }
        }
    }
}
