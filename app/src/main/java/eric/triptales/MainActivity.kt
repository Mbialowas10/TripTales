package eric.triptales

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import eric.triptales.screens.HomeScreen
import eric.triptales.screens.SearchScreen
import eric.triptales.viewmodel.PlacesViewModel
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import eric.triptales.screens.NearBySearchScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val placesViewModel: PlacesViewModel = viewModel()
//            MapScreen(placesViewModel = placesViewModel)
              App(placesViewModel)
        }
    }
}

@Composable
fun App(placesViewModel: PlacesViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {
        composable("home") { HomeScreen(navController) }
        composable("search") { SearchScreen(navController, placesViewModel) }
        composable("nearbySearch") { NearBySearchScreen(navController = navController,
            placesViewModel = placesViewModel)}
//        composable("blog") { BlogScreen(navController) }
//        composable("saved") { SavedPlacesScreen(navController) }
    }
}
