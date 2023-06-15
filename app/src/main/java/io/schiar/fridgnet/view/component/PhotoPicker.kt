package io.schiar.fridgnet.view.component

import android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalContext

@Composable
fun PhotoPicker(onURIsSelected: (uris: List<String>) -> Unit) {
    val context = LocalContext.current
    val multiplePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments(),
        onResult = { uris -> onURIsSelected(uris.map { uri ->
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    FLAG_GRANT_READ_URI_PERMISSION
                )
                uri.toString()
            })
        }
    )
    SideEffect { multiplePhotoPickerLauncher.launch(arrayOf("image/*")) }
}