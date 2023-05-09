package io.schiar.fridgnet.view.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import io.schiar.fridgnet.view.PhotoPicker
import io.schiar.fridgnet.view.util.AddressCreator
import io.schiar.fridgnet.view.util.toLatLng
import io.schiar.fridgnet.view.util.toPoint
import io.schiar.fridgnet.view.viewdata.LineStringLocationViewData
import io.schiar.fridgnet.view.viewdata.MultiPolygonLocationViewData
import io.schiar.fridgnet.view.viewdata.PolygonLocationViewData
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
                    viewModel.addAddressToImage(uri = uri, systemAddress = address)
                }
                isPhotoPickerShowing.invoke(false)
            }
        }

        LazyVerticalGrid(columns = GridCells.Fixed(4)) {
            imagesWithLocation.keys.map { address ->
                item {
                    val missionDoloresPark = LatLng(37.759773, -122.427063)
                    val target = if (imagesWithLocation[address]?.isNotEmpty() == true) {
                        imagesWithLocation[address]!![0].coordinate.toLatLng()
                    } else { missionDoloresPark }
                    val cameraPositionState = rememberCameraPositionState {
                        position = CameraPosition.fromLatLngZoom(target, 10f)
                    }
                    GoogleMap(
                        modifier = Modifier.size(Dp(100f)),
                        uiSettings = MapUiSettings(
                            compassEnabled = false,
                            zoomControlsEnabled = false,
                            zoomGesturesEnabled = false,
                            tiltGesturesEnabled = false,
                            indoorLevelPickerEnabled = false,
                            rotationGesturesEnabled = false
                        ),
                        cameraPositionState = cameraPositionState,
                        onMapClick = { _ ->
                            viewModel.selectImages(address = address)
                            onNavigateImage()
                        }
                    ) {
                        if (allLocationAddress.containsKey(address)) {
                            val location = allLocationAddress[address] ?: return@GoogleMap
                            val boundingBox = location.boundingBox
                            val cu = CameraUpdateFactory.newLatLngBounds(boundingBox, 2)
                            cameraPositionState.move(cu)

                            when (location) {
                                is LineStringLocationViewData -> {
                                    val lineStringRegion = location.region
                                    if (lineStringRegion.size == 1) {
                                        location.region[0].toPoint()
                                    } else {
                                        Polyline(points = location.region)
                                    }
                                }

                                is PolygonLocationViewData -> {
                                    location.region.map { Polyline(points = it) }
                                }

                                is MultiPolygonLocationViewData -> {
                                    location.region.map { polygonsLatLng ->
                                        polygonsLatLng.map { Polyline(points = it) }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
