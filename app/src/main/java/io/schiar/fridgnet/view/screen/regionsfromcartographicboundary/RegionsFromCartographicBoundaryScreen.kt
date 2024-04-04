package io.schiar.fridgnet.view.screen.regionsfromcartographicboundary

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toUpperCase
import io.schiar.fridgnet.R
import io.schiar.fridgnet.view.screen.regionsfromcartographicboundary.component.RegionsMapCheckableGrid
import io.schiar.fridgnet.view.util.ScreenInfo
import io.schiar.fridgnet.viewmodel.RegionsFromCartographicBoundaryViewModel

@Composable
fun RegionsFromCartographicBoundaryScreen(
    viewModel: RegionsFromCartographicBoundaryViewModel, info: (screenInfo: ScreenInfo) -> Unit
) {
    val cartographicBoundary by viewModel
        .currentCartographicBoundaryFlow
        .collectAsState(initial = null)
    val regions = cartographicBoundary?.regions ?: emptyList()
    val administrativeUnitName = cartographicBoundary?.administrativeUnitName

    info(
        ScreenInfo(
            title = administrativeUnitName ?: stringResource(id = R.string.polygons_screen),
            actions = {
                if (regions.size > 1) {
                    Button(
                        colors = buttonColors(containerColor = Color.Transparent),
                        onClick = viewModel::switchAll
                    ) {
                        Text(stringResource(id = R.string.switch_all).toUpperCase(Locale.current))
                    }
                }
            }
        )
    )

    RegionsMapCheckableGrid(
        regions = regions,
        onRegionCheckedChangeAt = viewModel::switchRegionAt
    )
}