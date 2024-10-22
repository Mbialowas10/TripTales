package eric.triptales.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import eric.triptales.api.PlaceResult

@Composable
fun PlaceCard(place: PlaceResult,
              type: String,
              onSearchNearbyClick: (placeId: String) -> Unit,
              navController: NavController) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(8.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Place,
                    contentDescription = "Place Icon",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = place.name,
                    fontSize = 20.sp,
                    color = Color.Black,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if(type === "autocomplete"){
                Button(
                    onClick = {
                        onSearchNearbyClick(place.placeId)
                        navController.navigate("nearbySearch")
                              } ,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Search Nearby")
                }
            } else if(type === "nearby"){
                Button(
                    onClick = { onSearchNearbyClick(place.placeId) } ,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("See Detail")
                }
            }


        }
    }
}
