package eric.triptales.components

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil.compose.rememberAsyncImagePainter

/**
 * Composable function for displaying an image from a URL.
 *
 * This function uses the Coil library to asynchronously load an image from the provided URL
 * and display it with specified scaling and modifiers.
 *
 * @param url The URL of the image to be displayed.
 * @param contentDescription A description of the image for accessibility purposes.
 * @param modifier [Modifier] to customize the appearance or behavior of the image.
 */
@Composable
fun ImageByURL(url: String, contentDescription: String?, modifier: Modifier = Modifier) {
    Image(
        painter = rememberAsyncImagePainter(url),
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = ContentScale.Crop
    )
}
