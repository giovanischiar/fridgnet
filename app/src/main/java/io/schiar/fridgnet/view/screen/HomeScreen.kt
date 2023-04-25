package io.schiar.fridgnet.view.screen

import android.annotation.SuppressLint
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
import io.schiar.fridgnet.viewmodel.MainViewModel

@SuppressLint("RestrictedApi")
@Composable
fun HomeScreen(viewModel: MainViewModel) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val context = LocalContext.current
        val images by viewModel.images.collectAsState()

        val multiplePhotoPickerLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetMultipleContents(),
            onResult = { uris ->
                uris.forEach { uri ->
                    context.contentResolver.openInputStream(uri)!!.use { ins ->
                        val exifInterface = ExifInterface(ins)
                        val latLng = exifInterface.latLong ?: doubleArrayOf(0.0, 0.0)
                        val date = exifInterface.dateTime
                        viewModel.addImage(
                            uri = uri.toString(),
                            date = date ?: 0L,
                            latitude = latLng[0],
                            longitude = latLng[1]
                        )
                    }
                }
            }
        )
        Button(onClick = { multiplePhotoPickerLauncher.launch("image/*") }) {
            Text("Pick Photo")
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