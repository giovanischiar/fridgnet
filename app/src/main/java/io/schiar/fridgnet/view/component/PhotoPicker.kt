package io.schiar.fridgnet.view.component

import android.annotation.SuppressLint
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.exifinterface.media.ExifInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun PhotoPicker(onPhotosPicked: (uri: String, date: Long, latitude: Double, longitude: Double) -> Unit) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val multiplePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents(),
        onResult = { uris ->
            coroutineScope.launch(Dispatchers.IO) {
                uris.forEach { uri ->
                    context.contentResolver.openInputStream(uri)!!.use { ins ->
                        val exifInterface = ExifInterface(ins)
                        val latLng = exifInterface.latLong ?: doubleArrayOf(0.0, 0.0)
                        @SuppressLint("RestrictedApi")
                        val date = exifInterface.dateTime
                        onPhotosPicked(uri.toString(), date ?: 0L, latLng[0], latLng[1])
                    }
                }
            }
        }
    )
    SideEffect { multiplePhotoPickerLauncher.launch("image/*") }
}