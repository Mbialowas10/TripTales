package eric.triptales

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import eric.triptales.screens.HomeScreen
import eric.triptales.screens.SearchScreen
import eric.triptales.viewmodel.PlacesViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import eric.triptales.screens.LoginScreen
import eric.triptales.screens.SavedPlacesScreen
import eric.triptales.screens.NearBySearchScreen
import eric.triptales.screens.PlaceDetailScreen
import eric.triptales.screens.StoriesScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseApp.initializeApp(this)

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

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(onLoginSuccess = { navController.navigate("home") })
        }
        composable("home") { HomeScreen(navController, viewModel = placesViewModel) }
        composable("search") { SearchScreen(navController, placesViewModel) }
        composable("nearbySearch") { NearBySearchScreen(navController = navController,
            placesViewModel = placesViewModel)}
        composable("placeDetail") { PlaceDetailScreen(navController = navController, viewModel = placesViewModel)}
//        composable("blog") { BlogScreen(navController) }
        composable("saved") { SavedPlacesScreen(navController, viewModel = placesViewModel) }
        composable("stories") { StoriesScreen(viewModel = placesViewModel, navController) }
    }
}
