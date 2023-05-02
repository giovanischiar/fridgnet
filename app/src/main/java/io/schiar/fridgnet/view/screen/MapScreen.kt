package io.schiar.fridgnet.view.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.schiar.fridgnet.view.PhotoPicker
import io.schiar.fridgnet.view.component.Map
import io.schiar.fridgnet.viewmodel.MainViewModel

@Composable
fun MapScreen(viewModel: MainViewModel) {
    Box(modifier = Modifier.fillMaxSize()) {
        val (photoPickerShowing, isPhotoPickerShowing) = remember { mutableStateOf(false) }
        val visibleImages by viewModel.visibleImages.collectAsState()

        Map(modifier = Modifier.fillMaxSize(), visibleImages = visibleImages) {
            viewModel.visibleAreaChanged(it)
        }

        Button(
            onClick = { isPhotoPickerShowing.invoke(true) },
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
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
    }
}