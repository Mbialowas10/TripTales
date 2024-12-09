package eric.triptales.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
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
import eric.triptales.BuildConfig
import eric.triptales.database.PlaceEntity

/**
 * A composable function that displays an image slider for a given [PlaceEntity].
 *
 * This slider uses image references stored in the [PlaceEntity.photos] list to fetch and display
 * images from the Google Places API. If no images are available, a fallback message is shown.
 *
 * @param place The [PlaceEntity] object containing photo references and related information.
 */
@OptIn(ExperimentalPagerApi::class)
@Composable
fun PlaceEntityImageSlider(place: PlaceEntity) {
    val photos = place.photos ?: emptyList() // Get photo references, default to an empty list
    val pagerState = rememberPagerState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (photos.isNotEmpty()) {
            /**
             * HorizontalPager to display images from the Google Places API.
             *
             * @param count Total number of photos to display.
             * @param state Manages the pager state, including the current page.
             */
            HorizontalPager(
                count = photos.size,
                state = pagerState,
                modifier = Modifier
                    .height(200.dp)
                    .fillMaxWidth()
            ) { page ->
                val photoReference = photos[page]
                val imageUrl =
                    "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=$photoReference&key=${BuildConfig.GOOGLE_MAPS_API_KEY}"

                /**
                 * Displays the image for the current page using [rememberAsyncImagePainter].
                 *
                 * @param painter Loads and caches images asynchronously.
                 * @param contentDescription A description of the image for accessibility.
                 */
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

            // Dot indicators for the image pager
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                /**
                 * Creates a dot indicator for each photo reference.
                 *
                 * Highlights the dot corresponding to the current page.
                 */
                repeat(photos.size) { index ->
                    val isSelected = pagerState.currentPage == index
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .size(if (isSelected) 10.dp else 8.dp)
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primary
                                else Color.Gray,
                                shape = androidx.compose.foundation.shape.CircleShape
                            )
                    )
                }
            }
        } else {
            // Fallback UI if no photos are available
            Box(
                modifier = Modifier
                    .height(200.dp)
                    .fillMaxWidth()
                    .background(Color.Gray),
                contentAlignment = Alignment.Center
            ) {
                /**
                 * Displays a message when no images are available for the slider.
                 */
                Text(text = "No Image Available", color = Color.White)
            }
        }
    }
}
