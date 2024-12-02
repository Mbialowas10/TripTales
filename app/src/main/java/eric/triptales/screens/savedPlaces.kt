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

@Composable
fun SavedPlacesScreen(navController: NavController ,viewModel: PlacesViewModel) {
    viewModel.getAllPlacesFromDB()
    val savedPlaces = viewModel.savedPlaces.value
    val sortedPlaces = savedPlaces.groupBy { parseCountry(it.address) }

    Scaffold(
        topBar = {
            eric.triptales.components.TopAppBar(
                title = "Saved Place",
                type = "main",
                navController = navController
            )
        },
        bottomBar = { BottomNavigationBar(selectedScreen = "Saved", navController = navController) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (savedPlaces.isEmpty()) {
                Text("No saved places yet.", style = MaterialTheme.typography.bodyMedium)
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
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

                        // Places under this country
                        items(countryPlaces) { place ->
                            SavedPlaceCard(place = place, viewModel = viewModel, navController = navController)
                        }
                    }
                }
            }
        }
    }
}
