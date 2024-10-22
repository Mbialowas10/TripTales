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

@Composable
fun NearBySearchScreen(navController: NavController, placesViewModel: PlacesViewModel){
    Scaffold(
        topBar = { TopAppBar("Search near by", "sub") },
        bottomBar = { BottomNavigationBar(selectedScreen = "Search", navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

//            Box(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .background(Color.Gray),
//                contentAlignment = Alignment.Center
//            ) {
//                if(places.isEmpty()){
//                    Text("No results found", modifier = Modifier.fillMaxSize())
//                } else {
//                    ListOfPlaces(places = places,"nearby" , placesViewModel, navController)
//                }
//            }
            MapScreen(placesViewModel = placesViewModel, navController = navController)
        }
    }
}