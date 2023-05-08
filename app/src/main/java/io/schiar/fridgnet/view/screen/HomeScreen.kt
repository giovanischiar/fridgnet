package io.schiar.fridgnet.view.screen

import android.graphics.Point
import android.util.Log
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
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import io.schiar.fridgnet.model.Address
import io.schiar.fridgnet.view.PhotoPicker
import io.schiar.fridgnet.view.util.*
import io.schiar.fridgnet.viewmodel.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
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
                    viewModel.addAddressToImage(uri, address.toAddress())
                }
                isPhotoPickerShowing.invoke(false)
            }
        }

        LazyVerticalGrid(columns = GridCells.Fixed(4)) {
            imagesWithLocation.keys.map { address ->
                item {
                    val missionDoloresPark = LatLng(37.759773, -122.427063)
                    val target = if (imagesWithLocation[address]?.isNotEmpty() == true) {
                        imagesWithLocation[address]!![0].location.toLatLng()
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
                        var point by remember { mutableStateOf<LatLng?>(null) }
                        var lineString by remember { mutableStateOf<List<LatLng>>(emptyList()) }
                        var polygons by remember { mutableStateOf<List<List<LatLng>>>(emptyList()) }
                        var multipolygons by remember { mutableStateOf<List<List<List<LatLng>>>>(emptyList()) }
                        val jobs = remember { mutableMapOf<Address, Job>() }
                        var latLngBounds: LatLngBounds? by remember { mutableStateOf(null) }

                        SideEffect {
                            if (!jobs.containsKey(address)) {
                                jobs[address] = coroutineScope.launch(Dispatchers.IO) {
                                    val result = withContext(Dispatchers.Default) {
                                        Log.d("api result", "searching $address")
                                        PolygonSearcher(address = address).search()
                                    }
                                    Log.d("api result", result.body().toString())
                                    val bodyList = (result.body() ?: emptyList())
                                    if (bodyList.isNotEmpty()) {
                                        val body = bodyList[0]
                                        val geoJson = body.geojson
                                        when(geoJson.type) {
                                            "Point" -> {
                                                val pointDouble = geoJson.coordinates as List<Double>
                                                point = pointDouble.toLatLng()
                                            }
                                            "LineString" -> {
                                                val polygonDouble = geoJson.coordinates as List<List<Double>>
                                                lineString = polygonDouble.toListLatLng()
                                            }

                                            "Polygon" -> {
                                                val polygonDouble = geoJson.coordinates as List<List<List<Double>>>
                                                polygons = polygonDouble.toMatrixLatLng()
                                            }

                                            "MultiPolygon" -> {
                                                val multipolygonDouble = geoJson.coordinates as List<List<List<List<Double>>>>
                                                multipolygons = multipolygonDouble.toListOfPolygon()
                                            }
                                        }

                                        if (body.boundingbox.size == 4) {
                                            latLngBounds = body.boundingbox.toLatLngBounds()
                                        }
                                    }
                                }
                            }
                        }

                        if (latLngBounds != null) {
                            val cu = CameraUpdateFactory.newLatLngBounds(latLngBounds!!, 2)
                            cameraPositionState.move(cu)
                        }

                        if (point != null) {
                            Log.d("api result", "$point")
                            Point(point!!.latitude.toInt(), point!!.longitude.toInt())
                        }

                        if (lineString.isNotEmpty()) {
                            Log.d("api result", "$lineString")
                            Polyline(points = lineString)
                        }

                        if (polygons.isNotEmpty()) {
                            Log.d("api result", "$polygons")
                            polygons.map {
                                Polyline(points = it)
                            }
                        }

                        if (multipolygons.isNotEmpty()) {
                            Log.d("api result", "$multipolygons")
                            multipolygons.map { polygonsLatLng ->
                                polygonsLatLng.map {
                                    Polyline(points = it)
                                }
                            }

                        }
                    }
                }
            }
        }
    }
}
