package io.schiar.fridgnet.view.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import io.schiar.fridgnet.view.util.toImageBitmap
import io.schiar.fridgnet.view.viewdata.ImageViewData

@Composable
fun PhotoGrid(images: List<ImageViewData>) {
    val sortedImages = images.sortedBy { it.date }.reversed()
    LazyVerticalGrid(columns = GridCells.Fixed(6)) {
        items(images.size) { index ->
            Image(
                modifier = Modifier.height(75.dp),
                bitmap = sortedImages[index].byteArray.toImageBitmap(),
                contentDescription = "some useful description",
                contentScale = ContentScale.Crop
            )
        }
    }
}