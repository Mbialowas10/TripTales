package eric.triptales.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState

/**
 * A composable function that displays a slider of images using bitmaps.
 *
 * This function uses the HorizontalPager from Accompanist to create a horizontally scrollable
 * image slider with dot indicators to show the current page. If no images are provided, it
 * displays a fallback message.
 *
 * @param images A list of [Bitmap] objects representing the images to display.
 */
@OptIn(ExperimentalPagerApi::class)
@Composable
fun ImageSliderByBitmap(images: List<Bitmap>) {
    val pagerState = rememberPagerState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (images.isNotEmpty()) {
            // HorizontalPager to display images
            HorizontalPager(
                count = images.size,
                state = pagerState,
                modifier = Modifier
                    .height(300.dp)
                    .fillMaxWidth()
            ) { page ->
                /**
                 * Displays each image in the pager.
                 *
                 * @param page The current page index.
                 */
                Image(
                    bitmap = images[page].asImageBitmap(),
                    contentDescription = "Place Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Dot indicators for the pager
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                /**
                 * Repeats to create a dot indicator for each image.
                 *
                 * Highlights the dot for the currently selected page.
                 */
                repeat(images.size) { index ->
                    val isSelected = pagerState.currentPage == index
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .size(if (isSelected) 10.dp else 8.dp)
                            .background(
                                color = if (isSelected) Color.Black else Color.Gray,
                                shape = androidx.compose.foundation.shape.CircleShape
                            )
                    )
                }
            }
        } else {
            // Fallback message when there are no images
            Box(
                modifier = Modifier
                    .height(200.dp)
                    .fillMaxWidth()
                    .background(Color.Gray),
                contentAlignment = Alignment.Center
            ) {
                /**
                 * Displays a message when no images are available.
                 */
                Text(text = "No Image Available")
            }
        }
    }
}
