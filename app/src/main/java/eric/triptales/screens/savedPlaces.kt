package eric.triptales.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import eric.triptales.components.BottomNavigationBar
import eric.triptales.components.SavedPlaceCard
import eric.triptales.utility.parseCountry
import eric.triptales.viewmodel.PlacesViewModel

/**
 * Displays the Saved Places screen where users can view their saved places grouped by country.
 *
 * This screen retrieves saved places from the database using the [PlacesViewModel], groups them
 * by country, and displays each group along with its places. If there are no saved places, a
 * message is displayed.
 *
 * @param navController The [NavController] for handling navigation actions.
 * @param viewModel The [PlacesViewModel] for managing place-related data and operations.
 */
@Composable
fun SavedPlacesScreen(navController: NavController, viewModel: PlacesViewModel) {
    // Fetch saved places from the database
    viewModel.getAllPlacesFromDB()
    val savedPlaces = viewModel.savedPlaces.value
    val sortedPlaces = savedPlaces.groupBy { parseCountry(it.address) }

    Scaffold(
        topBar = {
            // Top app bar with a title
            eric.triptales.components.TopAppBar(
                title = "Saved Place",
                type = "main",
                navController = navController
            )
        },
        bottomBar = {
            // Bottom navigation bar with "Saved" selected
            BottomNavigationBar(selectedScreen = "Saved", navController = navController)
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize() // Occupy the full available space
                .padding(padding) // Scaffold padding
                .padding(16.dp), // Inner padding
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Display a message if no places are saved
            if (savedPlaces.isEmpty()) {
                Text("No saved places yet.", style = MaterialTheme.typography.bodyMedium)
            } else {
                // Display saved places grouped by country
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize() // Fill the available vertical space
                ) {
                    // Iterate over grouped places
                    sortedPlaces.forEach { (country, countryPlaces) ->
                        item {
                            // Country Header
                            Text(
                                text = country,
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        // List items for places under the country
                        items(countryPlaces) { place ->
                            SavedPlaceCard(
                                place = place,
                                viewModel = viewModel,
                                navController = navController
                            )
                        }
                    }
                }
            }
        }
    }
}
