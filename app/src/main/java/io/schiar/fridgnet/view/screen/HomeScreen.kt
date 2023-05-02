package io.schiar.fridgnet.view.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.schiar.fridgnet.view.PhotoPicker
import io.schiar.fridgnet.view.component.PhotoGrid
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
        Button(onClick = { isPhotoPickerShowing.invoke(true) }) {
            Text("Add Photos")
        }
        if (photoPickerShowing) {
            PhotoPicker { uri, date, latitude, longitude ->
                viewModel.addImage(
                    uri = uri,
                    date = date,
                    latitude = latitude,
                    longitude = longitude
                )
                isPhotoPickerShowing.invoke(false)
            }
        }
        PhotoGrid(images = images)
    }
}