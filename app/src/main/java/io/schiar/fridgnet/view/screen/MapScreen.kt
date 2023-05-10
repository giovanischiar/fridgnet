package io.schiar.fridgnet.view.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import io.schiar.fridgnet.view.PhotoPicker
import io.schiar.fridgnet.view.component.Map
import io.schiar.fridgnet.view.util.AddressCreator
import io.schiar.fridgnet.viewmodel.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun MapScreen(viewModel: MainViewModel) {
    Box(modifier = Modifier.fillMaxSize()) {
        val coroutineScope = rememberCoroutineScope()
        val context = LocalContext.current
        val (photoPickerShowing, isPhotoPickerShowing) = remember { mutableStateOf(false) }
        val visibleImages by viewModel.visibleImages.collectAsState()
        val countries by viewModel.allCountries.collectAsState()
        val states by viewModel.allStates.collectAsState()
        val counties by viewModel.allCounties.collectAsState()
        val cities by viewModel.allCities.collectAsState()

        Map(
            modifier = Modifier.fillMaxSize(),
            visibleImages = visibleImages,
            countries = countries,
            states = states,
            counties = counties,
            cities = cities
        ) {
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
                coroutineScope.launch(Dispatchers.IO) {
                    val address = withContext(Dispatchers.Default) {
                        AddressCreator().addressFromLocation(
                            context = context,
                            latitude = latitude,
                            longitude = longitude
                        )
                    }
                    viewModel.addAddressToImage(uri = uri, systemAddress = address)
                }
                isPhotoPickerShowing.invoke(false)
            }
        }
    }
}