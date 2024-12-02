package eric.triptales.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import eric.triptales.components.BottomNavigationBar
import eric.triptales.components.TopAppBar
import eric.triptales.firebase.entity.PlannedTrip
import eric.triptales.viewmodel.DirectionsViewModel

@Composable
fun PlannedTripScreen(
    navController: NavController,
    viewModel: DirectionsViewModel,
) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid

    LaunchedEffect(userId) {
        userId?.let {
            viewModel.fetchPlannedTrips(userId)
        }
    }

    Scaffold(
        topBar = { TopAppBar("Planned Trip", "main", navController) },
        bottomBar = { BottomNavigationBar("Plan", navController) },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("tripPicking") }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Trip")
            }
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                Text(
                    text = "Planned Trips",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(16.dp)
                )

                if (viewModel.plannedTrips.isEmpty()) {
                    Text(
                        text = "No planned trips. Add one by clicking the + button.",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                } else {
                    LazyColumn {
                        items(viewModel.plannedTrips) { trip ->
                            TripItem(trip = trip, navController, viewModel)
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun TripItem(trip: PlannedTrip, navController: NavController, viewModel: DirectionsViewModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = trip.name,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "From: ${trip.origin.name} to ${trip.destination.name}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Button(onClick = {
                val waypointsId = trip.waypoints.map {
                    it.placeId
                }
                viewModel.updateSelectedPlaces(trip.origin, trip.destination, trip.waypoints)
                viewModel.fetchRoutes(trip.origin.placeId, trip.destination.placeId, waypointsId)
                viewModel.tripDetailReadonly.value = true
                navController.navigate("tripDetail")
            }) {
                Text("See Details")
            }
        }
    }
}
