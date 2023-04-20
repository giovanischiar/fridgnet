package io.schiar.fridgnet.view.screen

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.exifinterface.media.ExifInterface
import coil.compose.AsyncImage
import io.schiar.fridgnet.view.viewdata.ImageViewData
import io.schiar.fridgnet.view.viewdata.LocationViewData

@Composable
fun HomeScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Hello world")
        val context = LocalContext.current
        var selectedImageUris by remember { mutableStateOf<List<ImageViewData>>(emptyList()) }
        val multiplePhotoPickerLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetMultipleContents(),
            onResult = { uris ->
                selectedImageUris = uris.map {
                    context.contentResolver.openInputStream(it)!!.use { ins ->
                        val exifInterface = ExifInterface(ins)
                        val latLng = exifInterface.latLong
                        val date = exifInterface.dateTime
                        val locationViewData = latLng?.let { doubleArray ->
                            LocationViewData(
                                lat = doubleArray[0].toString(),
                                lng = doubleArray[1].toString()
                            )
                        } ?: LocationViewData(lat = "0", lng = "0")
                        ImageViewData(
                            uri = it,
                            date = date.toString(),
                            location = locationViewData
                        )
                    }
                }
            }
        )
        Button (onClick = { multiplePhotoPickerLauncher.launch("image/*") }){
            Text("Pick Photo")
        }
        Photos(selectedImageUris = selectedImageUris)
    }
}

@Composable
fun Photos(selectedImageUris: List<ImageViewData>) {
    LazyColumn {
        items(selectedImageUris) { imageViewData ->
            val (uri, date, location) = imageViewData
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