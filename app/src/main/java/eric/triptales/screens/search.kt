package eric.triptales.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.* // Layout utilities
import androidx.compose.material3.* // Material Design 3 components
import androidx.compose.runtime.Composable // State management
import androidx.compose.ui.Modifier // Modifier utilities
import androidx.compose.ui.graphics.Color // Color utilities
import androidx.compose.ui.Alignment // Alignment utilities
import androidx.compose.ui.unit.dp // Size units
import androidx.navigation.NavController // Navigation controller
import eric.triptales.components.TopAppBar // Custom top app bar
import eric.triptales.components.BottomNavigationBar // Custom bottom navigation bar
import eric.triptales.components.ListOfPlaces // Component for displaying a list of places
import eric.triptales.components.SearchBar // Search bar component
import eric.triptales.viewmodel.PlacesViewModel // ViewModel for place management

/**
 * Displays the Search Screen where users can search for places and view the results.
 *
 * This screen includes a search bar at the top for entering queries and a list of places below.
 * If no results are found, a placeholder message is shown.
 *
 * @param navController The [NavController] for navigation actions.
 * @param placesViewModel The [PlacesViewModel] used for managing search results and queries.
 */
@Composable
fun SearchScreen(navController: NavController, placesViewModel: PlacesViewModel) {
    val places = placesViewModel.autocompleteResults.value // Live search results

    Scaffold(
        topBar = { TopAppBar("Search", "main", navController) }, // App bar with a title
        bottomBar = { BottomNavigationBar(selectedScreen = "Search", navController) } // Bottom navigation bar
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize() // Fill the available vertical space
                .padding(paddingValues) // Add padding from the Scaffold
        ) {
            // Search Bar for entering queries
            SearchBar(placesViewModel)

            // Placeholder or list of results
            Box(
                modifier = Modifier
                    .fillMaxSize() // Fill the remaining vertical space
                    .background(Color.White), // Background color
                contentAlignment = Alignment.Center // Center the content
            ) {
                if (places.isEmpty()) {
                    // Display placeholder text when no results are found
                    Text(
                        "No results found",
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 0.dp, vertical = 10.dp)
                    )
                } else {
                    // Display the list of places if results are available
                    ListOfPlaces(
                        places = places,
                        type = "autocomplete",
                        viewModel = placesViewModel,
                        navController = navController
                    )
                }
            }
        }
    }
}
