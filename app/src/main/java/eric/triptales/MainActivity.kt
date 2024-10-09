package eric.triptales

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import eric.triptales.components.MapScreen
import eric.triptales.viewmodel.PlacesViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val placesViewModel: PlacesViewModel = viewModel()
            MapScreen(placesViewModel = placesViewModel)
        }
    }
}
