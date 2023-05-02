package io.schiar.fridgnet.view.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import coil.compose.AsyncImage
import io.schiar.fridgnet.view.viewdata.ImageViewData

@Composable
fun PhotoGrid(images: List<ImageViewData>) {
    val sortedImages = images.sortedBy { it.date }.reversed()
    LazyVerticalGrid(columns = GridCells.Fixed(4)) {
        items(images.size) { index ->
            val (uri) = sortedImages[index]
            Column(modifier = Modifier.size(Dp(value = 100f))) {
                AsyncImage(
                    model = uri,
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}