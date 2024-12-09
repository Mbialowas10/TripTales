package eric.triptales

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import eric.triptales.screens.*
import eric.triptales.viewmodel.PlacesViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.FirebaseApp
import eric.triptales.utility.ToastUtil
import eric.triptales.viewmodel.DirectionsViewModel

/**
 * MainActivity serves as the entry point for the application.
 *
 * It initializes Firebase, sets up the app's content, and initializes required
 * utilities such as the global ToastUtil. The main navigation graph is set up
 * using Jetpack Compose Navigation.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase and custom utilities.
        FirebaseApp.initializeApp(this)
        ToastUtil.initialize(this)

        // Set the content for the app.
        setContent {
            val placesViewModel: PlacesViewModel = viewModel()
            val directionViewModel: DirectionsViewModel = viewModel()

            // Start the app with the main navigation graph.
            App(placesViewModel, directionViewModel)
        }
    }
}

/**
 * Composable function for the main navigation graph.
 *
 * Sets up navigation between different screens using Jetpack Compose Navigation.
 *
 * @param placesViewModel The `PlacesViewModel` instance shared across screens.
 * @param directionViewModel The `DirectionsViewModel` instance shared across screens.
 */
@Composable
fun App(placesViewModel: PlacesViewModel, directionViewModel: DirectionsViewModel) {
    val navController = rememberNavController()

    // Navigation host defining the app's navigation graph.
    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            // Login screen with navigation to home on successful login.
            LoginScreen(onLoginSuccess = { navController.navigate("home") })
        }
        composable("home") {
            // Home screen displaying main content.
            HomeScreen(navController, viewModel = placesViewModel)
        }
        composable("search") {
            // Search screen for finding places.
            SearchScreen(navController, placesViewModel)
        }
        composable("nearbySearch") {
            // Nearby search screen for finding attractions near a location.
            NearBySearchScreen(navController = navController, placesViewModel = placesViewModel)
        }
        composable("placeDetail") {
            // Place detail screen displaying information about a specific place.
            PlaceDetailScreen(navController = navController, viewModel = placesViewModel)
        }
        composable("plan") {
            // Planned trip screen for viewing or managing planned trips.
            PlannedTripScreen(navController = navController, viewModel = directionViewModel)
        }
        composable("tripPicking") {
            // Place picking screen for selecting trip origins, destinations, and waypoints.
            PlacePickingScreen(directionViewModel = directionViewModel, navController)
        }
        composable("tripDetail") {
            // Trip detail screen showing route details and trip information.
            TripDetailScreen(directionViewModel, navController)
        }
        composable("saved") {
            // Saved places screen for viewing and managing saved locations.
            SavedPlacesScreen(navController, viewModel = placesViewModel)
        }
        composable("stories") {
            // Stories screen for viewing or sharing stories related to places.
            StoriesScreen(viewModel = placesViewModel, navController)
        }
        composable("account") {
            // Account screen for managing user profile and settings.
            AccountScreen(navController)
        }
    }
}
