package io.schiar.fridgnet.view

import android.annotation.SuppressLint
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalContext
import androidx.exifinterface.media.ExifInterface

@Composable
fun PhotoPicker(onPhotosPicked: (uri: String, date: Long, latitude: Double, longitude: Double) -> Unit) {
    val context = LocalContext.current
    val multiplePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents(),
        onResult = { uris ->
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
    )
    SideEffect { multiplePhotoPickerLauncher.launch("image/*") }
}