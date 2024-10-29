package eric.triptales.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import eric.triptales.api.PlaceResult
import eric.triptales.viewmodel.PlacesViewModel

@Composable
fun ListOfPlaces(places: List<PlaceResult>, type: String, viewModel: PlacesViewModel, navController: NavController) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(places) { place ->
            PlaceCard(place = place, type, viewModel, navController)
        }
    }
}
