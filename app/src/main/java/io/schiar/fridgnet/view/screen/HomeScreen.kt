package io.schiar.fridgnet.view.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import io.schiar.fridgnet.view.PhotoPicker
import io.schiar.fridgnet.view.component.MapPhotoItem
import io.schiar.fridgnet.view.util.AddressCreator
import io.schiar.fridgnet.viewmodel.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun HomeScreen(viewModel: MainViewModel, onNavigateImage: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LaunchedEffect(Unit) { viewModel.subscribeLocationRepository() }
        val imagesWithLocation by viewModel.imagesWithLocation.collectAsState()
        val allLocationAddress by viewModel.allLocationAddress.collectAsState()

        val (photoPickerShowing, isPhotoPickerShowing) = remember { mutableStateOf(false) }
        val coroutineScope = rememberCoroutineScope()
        val context = LocalContext.current

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
                coroutineScope.launch(Dispatchers.IO) {
                    val address = withContext(Dispatchers.Default) {
                        AddressCreator().addressFromLocation(
                            context = context,
                            latitude = latitude,
                            longitude = longitude
                        )
                    }
                    viewModel.addAddressToImage(
                        uri = uri,
                        locality = address.locality,
                        subAdminArea = address.subAdminArea,
                        adminArea = address.adminArea,
                        countryName = address.countryName
                    )
                }
                isPhotoPickerShowing.invoke(false)
            }
        }

        LazyVerticalGrid(columns = GridCells.Fixed(4)) {
            imagesWithLocation.entries.map { (address, images) ->
                item {
                    MapPhotoItem(
                        initialLocation = images[0].coordinate,
                        location = allLocationAddress[address]
                    ) {
                        viewModel.selectImages(address)
                        onNavigateImage()
                    }
                }
            }
        }
    }
}
