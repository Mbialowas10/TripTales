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

/**
 * A composable function that displays a list of places using a lazy column.
 *
 * Each item in the list is rendered using the [PlaceCard] composable, and its behavior
 * is determined by the provided `type` parameter. This list is scrollable and efficiently
 * handles large data sets.
 *
 * @param places A [List] of [PlaceResult] representing the places to display.
 * @param type A [String] indicating the type of card ("nearby" or "autocomplete") for each place.
 * @param viewModel The [PlacesViewModel] used to interact with the place data.
 * @param navController The [NavController] used for navigation actions.
 */
@Composable
fun ListOfPlaces(
    places: List<PlaceResult>,
    type: String,
    viewModel: PlacesViewModel,
    navController: NavController
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(), // Fills the available screen space
        contentPadding = PaddingValues(16.dp) // Adds padding around the list
    ) {
        /**
         * Generates a list of [PlaceCard] components, one for each place in the [places] list.
         */
        items(places) { place ->
            PlaceCard(
                place = place,
                type = type,
                viewModel = viewModel,
                navController = navController
            )
        }
    }
}
