package io.schiar.fridgnet.view.administrationunit.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import io.schiar.fridgnet.view.shared.util.toImageBitmap
import io.schiar.fridgnet.view.shared.viewdata.ImageViewData

/**
 * The component that represents a grid of photos.
 *
 * @param modifier the modifier used in the internal [LazyVerticalGrid].
 * @param images the list of image data objects to display.
 */
@Composable
fun PhotoGrid(modifier: Modifier, images: List<ImageViewData>) {
    val sortedImages = images.sortedBy { it.date }.reversed()
    LazyVerticalGrid(modifier = modifier, columns = GridCells.Fixed(6)) {
        items(images.size) { index ->
            Image(
                modifier = Modifier.height(75.dp),
                bitmap = sortedImages[index].byteArray.toImageBitmap(),
                contentDescription = "Photo from phone",
                contentScale = ContentScale.Crop
            )
        }
    }
}