package eric.triptales.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Alignment
import androidx.navigation.NavController
import eric.triptales.components.TopAppBar
import eric.triptales.components.BottomNavigationBar
import eric.triptales.components.ListOfPlaces
import eric.triptales.components.SearchBar
import eric.triptales.viewmodel.PlacesViewModel

@Composable
fun SearchScreen(navController: NavController, placesViewModel: PlacesViewModel) {
    val places = placesViewModel.autocompleteResults.value

    Scaffold(
        topBar = { TopAppBar("Search", "main", navController) },
        bottomBar = { BottomNavigationBar(selectedScreen = "Search", navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search Bar
            SearchBar(placesViewModel)

            // Placeholder for search results
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Gray),
                contentAlignment = Alignment.Center
            ) {
                if(places.isEmpty()){
                    Text("No results found", modifier = Modifier.fillMaxSize())
                } else {
                    ListOfPlaces(places = places, "autocomplete" ,placesViewModel, navController)
                }
            }
        }
    }
}

