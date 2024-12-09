package eric.triptales.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.input.ImeAction
import eric.triptales.viewmodel.PlacesViewModel

/**
 * A composable function that provides a search bar with dropdown functionality.
 *
 * The `SearchBar` allows users to enter a search query and select a place type (e.g., "geocode",
 * "address"). The selected type is used for filtering place autocomplete results through the
 * [PlacesViewModel].
 *
 * @param viewModel The [PlacesViewModel] used to fetch autocomplete results based on the query and selected type.
 */
@Composable
fun SearchBar(viewModel: PlacesViewModel) {
    // Retrieve the current search query from the ViewModel
    val searchQuery = viewModel.searchQuery.value

    // Define available place types for filtering
    val placeTypes = listOf("geocode", "address", "establishment")
    var selectedType by remember { mutableStateOf(placeTypes[0]) } // Default to the first type
    var expanded by remember { mutableStateOf(false) } // Control dropdown visibility

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(Color.White),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Search icon
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "Search Icon",
            modifier = Modifier.padding(8.dp)
        )

        // Search input field
        BasicTextField(
            value = searchQuery,
            onValueChange = { newValue ->
                viewModel.findPlaceAutocomplete(newValue, selectedType)
            },
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = {
                viewModel.findPlaceAutocomplete(searchQuery, selectedType)
            }),
            modifier = Modifier
                .weight(1f) // Take up available horizontal space
                .padding(8.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Dropdown menu for selecting the place type
        Box(
            modifier = Modifier
                .background(Color.LightGray)
                .padding(horizontal = 8.dp, vertical = 4.dp)
                .clickable { expanded = !expanded }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = selectedType) // Display selected type
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Dropdown Arrow"
                )
            }

            // Dropdown menu items
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                placeTypes.forEach { type ->
                    DropdownMenuItem(
                        text = { Text(type) },
                        onClick = {
                            selectedType = type // Update selected type
                            expanded = false
                            viewModel.findPlaceAutocomplete(searchQuery, selectedType) // Trigger search
                        }
                    )
                }
            }
        }
    }
}
