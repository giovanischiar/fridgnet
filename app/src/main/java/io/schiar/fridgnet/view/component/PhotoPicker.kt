package io.schiar.fridgnet.view.component

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect

@Composable
fun PhotoPicker(onURIsSelected: (uris: List<String>) -> Unit) {
    val multiplePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents(),
        onResult = { uris -> onURIsSelected(uris.map { it.toString() }) }
    )
    SideEffect { multiplePhotoPickerLauncher.launch("image/*") }
}