package eric.triptales.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import eric.triptales.api.Route
import eric.triptales.components.TopAppBar

@Composable
fun TripDetailScreen(
    tripName: String,
    route: Route,
    places: List<String>,
    onBack: () -> Unit
) {
    Scaffold(
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
//            // Map Section
//            Box(modifier = Modifier.weight(1f)) {
//                TripDetailMap(route = route, places = places)
//            }
//
//            // Route Information Section
//            RouteInfo(route = route)
//
//            // Trip Details Section
//            TripDetails(places = places)
        }
    }
}
