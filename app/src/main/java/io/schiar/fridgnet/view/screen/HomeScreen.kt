package io.schiar.fridgnet.view.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
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
        val images by viewModel.visibleImages.collectAsState()
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
    LazyColumn {
        items(images) { image ->
            val (uri, date, location) = image
            val (lat, lng) = location
            Column {
                AsyncImage(
                    model = uri,
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.Crop
                )
                Row {
                    Text("Date:")
                    Text(date)
                }
                Row {
                    Text("Location:")
                    Text("($lat, $lng)")
                }
            }
        }
    }
}