package io.schiar.fridgnet.view.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import coil.compose.AsyncImage
import io.schiar.fridgnet.view.PhotoPicker
import io.schiar.fridgnet.view.viewdata.ImageViewData
import io.schiar.fridgnet.viewmodel.MainViewModel

@Composable
fun HomeScreen(viewModel: MainViewModel) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val images by viewModel.allImages.collectAsState()
        val (photoPickerShowing, isPhotoPickerShowing) = remember { mutableStateOf(false) }
        Button( onClick = { isPhotoPickerShowing.invoke(true) }) {
            Text("Add Photos")
        }
        if (photoPickerShowing) {
            PhotoPicker { uri, date, latitude, longitude ->
                viewModel.addImage(uri = uri, date = date, latitude = latitude, longitude = longitude)
                isPhotoPickerShowing.invoke(false)
            }
        }
        Photos(images = images)
    }
}
@Composable
fun Photos(images: List<ImageViewData>) {
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