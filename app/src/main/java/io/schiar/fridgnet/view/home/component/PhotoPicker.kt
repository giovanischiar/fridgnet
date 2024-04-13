package io.schiar.fridgnet.view.home.component

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect

/**
 * A composable that launches the system photo picker screen, allowing the user to select multiple
 * images.
 *
 * @param onURIsSelected a callback function that receives a list of URIs representing the
 * selected photos.
 */
@Composable
fun PhotoPicker(onURIsSelected: (uris: List<String>) -> Unit) {
    val multiplePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments(),
        onResult = { uris -> onURIsSelected(uris.map { uri -> uri.toString() }) }
    )
    SideEffect { multiplePhotoPickerLauncher.launch(arrayOf("image/*")) }
}