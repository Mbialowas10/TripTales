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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import eric.triptales.components.BottomNavigationBar
import eric.triptales.components.TopAppBar
import eric.triptales.firebase.PlannedTrip
import eric.triptales.viewmodel.DirectionsViewModel

@Composable
fun PlannedTripScreen(
    navController: NavController,
    viewModel: DirectionsViewModel
) {
    val plannedTrips = viewModel.plannedTrips

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

                if (plannedTrips.isEmpty()) {
                    Text(
                        text = "No planned trips. Add one by clicking the + button.",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                } else {
                    LazyColumn {
                        items(plannedTrips) { trip ->
                            TripItem(trip = trip, navController)
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun TripItem(trip: PlannedTrip, navController: NavController) {
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
            Text(
                text = trip.tripName,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
            Button(onClick = { navController.navigate("tripPicking") }) {
                Text("See Details")
            }
        }
    }
}
