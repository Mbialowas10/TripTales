package eric.triptales.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import eric.triptales.api.PlaceDetailResult

@OptIn(ExperimentalPagerApi::class)
@Composable
fun PlaceDetailImageSlider(placeDetail: PlaceDetailResult) {
    val photos = placeDetail.photos ?: emptyList()
    val pagerState = rememberPagerState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (photos.isNotEmpty()) {
            HorizontalPager(
                count = photos.size,
                state = pagerState,
                modifier = Modifier
                    .height(200.dp)
                    .fillMaxWidth()
            ) { page ->
                val photoReference = photos[page].photo_reference
                val imageUrl = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=$photoReference&key=AIzaSyBQtniS0NCgJc5D5g_t_ke42u5_ttYn4Rw"

                Image(
                    painter = rememberAsyncImagePainter(imageUrl),
                    contentDescription = "Place Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Dot Indicators
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                repeat(photos.size) { index ->
                    val isSelected = pagerState.currentPage == index
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .size(if (isSelected) 10.dp else 8.dp)
                            .background(if (isSelected) Color.Black else Color.Gray, shape = androidx.compose.foundation.shape.CircleShape)
                    )
                }
            }
        } else {
            // Fallback if no photos are available
            Box(
                modifier = Modifier
                    .height(200.dp)
                    .fillMaxWidth()
                    .background(Color.Gray),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "No Image Available")
            }
        }
    }
}
