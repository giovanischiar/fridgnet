package io.schiar.fridgnet.view.shared.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.maps.android.compose.CameraPositionState
import io.schiar.fridgnet.R
import io.schiar.fridgnet.view.shared.util.updateCameraPositionTo
import io.schiar.fridgnet.view.shared.viewdata.BoundingBoxViewData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * A column of Floting Action Buttons used for zoom controls in the map.
 *
 * @param modifier the modifier used for the internal [Column].
 * @param cameraPositionState the object provided by GoogleMaps to manipulate the camera state of
 * the map to perform zoom actions.
 * @param boundingBox the bounding Box of the regions used for zoom to the fit action.
 * @param imagesBoundingBox the bounding Box of the images used for zoom to the fit action.
 * @param enabled: Boolean if the buttons are enabled or not.
 */
@Composable
fun ZoomControls(
    modifier: Modifier = Modifier,
    cameraPositionState: CameraPositionState,
    boundingBox: BoundingBoxViewData? = null,
    imagesBoundingBox: BoundingBoxViewData? = null,
    enabled: Boolean
) {
    var zoomedOut by remember { mutableStateOf(value = true) }
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(15.dp)) {
        if (imagesBoundingBox != null && boundingBox != null) {
            FloatingActionButton(
                containerColor = colorResource(id = R.color.indigo_dye_500).copy(alpha = 0.40f),
                elevation = FloatingActionButtonDefaults.elevation(0.dp),
                onClick = {
                    val boundingBoxToMove = if (zoomedOut) imagesBoundingBox else boundingBox
                    if (!enabled) return@FloatingActionButton
                    cameraPositionState.updateCameraPositionTo(
                        boundingBox = boundingBoxToMove,
                        coroutineScope = coroutineScope,
                        animate = true,
                        padding = 27
                    )
                    zoomedOut = !zoomedOut
                }
            ) {
                val iconID = if (zoomedOut) R.drawable.ic_zoom_in_map else R.drawable.ic_fit_screen
                val iconContentDescription = if (zoomedOut) {
                    "Zoom to fit all photos"
                } else {
                    "Zoom to fit the whole cartographic boundary"
                }
                Icon(
                    painter = painterResource(id = iconID),
                    contentDescription = iconContentDescription,
                    tint = colorResource(id = R.color.white).copy(alpha = 0.40f)
                )
            }
        }

        FloatingActionButton(
            containerColor = colorResource(id = R.color.indigo_dye_500).copy(alpha = 0.40f),
            elevation = FloatingActionButtonDefaults.elevation(0.dp),
            onClick = {
                if (!enabled) return@FloatingActionButton
                coroutineScope.launch(Dispatchers.Main) {
                    cameraPositionState.animate(CameraUpdateFactory.zoomIn(), durationMs = 500)
                }
            }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_zoom_in),
                contentDescription = "Zoom in",
                tint = colorResource(id = R.color.white).copy(alpha = 0.40f)
            )
        }

        FloatingActionButton(
            containerColor = colorResource(id = R.color.indigo_dye_500).copy(alpha = 0.40f),
            elevation = FloatingActionButtonDefaults.elevation(0.dp),
            onClick = {
                if (!enabled) return@FloatingActionButton
                coroutineScope.launch(Dispatchers.Main) {
                    cameraPositionState.animate(CameraUpdateFactory.zoomOut(), durationMs = 500)
                }
            }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_zoom_out),
                contentDescription = "Zoom out",
                tint = colorResource(id = R.color.white).copy(alpha = 0.40f)
            )
        }
    }
}