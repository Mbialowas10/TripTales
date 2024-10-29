package eric.triptales.components

import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(title:String, type: String, navController: NavController) {
    TopAppBar(
        title = {
            Text(text = title)
        },
        navigationIcon = {
            if(type === "main"){
                IconButton(onClick = {  }) {
                    Icon(Icons.Default.Menu, contentDescription = "Menu")
                }
            } else if(type === "sub") {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft , contentDescription = "Back")
                }
            }

        },
        colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(
            containerColor = Color.LightGray,
            titleContentColor = Color.Black
        )
    )
}
