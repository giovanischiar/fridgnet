package io.schiar.fridgnet.view.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.maps.android.compose.CameraPositionState
import io.schiar.fridgnet.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun ZoomControls(
    modifier: Modifier,
    cameraPositionState: CameraPositionState,
    enabled: Boolean
) {
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        FloatingActionButton(
            containerColor = colorResource(id = R.color.indigo_dye_500).copy(alpha = 0.40f),
            elevation = FloatingActionButtonDefaults.elevation(0.dp),
            onClick = {
                if (enabled) {
                    coroutineScope.launch(Dispatchers.Main) {
                        cameraPositionState.animate(CameraUpdateFactory.zoomIn(), 500)
                    }
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
                if (enabled) {
                    coroutineScope.launch(Dispatchers.Main) {
                        cameraPositionState.animate(CameraUpdateFactory.zoomOut(), 500)
                    }
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