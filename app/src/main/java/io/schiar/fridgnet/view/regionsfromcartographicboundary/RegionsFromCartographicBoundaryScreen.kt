package io.schiar.fridgnet.view.regionsfromcartographicboundary

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
import androidx.hilt.navigation.compose.hiltViewModel
import io.schiar.fridgnet.R
import io.schiar.fridgnet.view.home.util.ScreenInfo
import io.schiar.fridgnet.view.regionsfromcartographicboundary.component.RegionsMapCheckableGrid
import io.schiar.fridgnet.viewmodel.RegionsFromCartographicBoundaryViewModel

/**
 * The component representing the Regions From Cartographic Boundary Screen. It displays all regions
 * that belongs to a single Cartographic Boundary. It allows check or uncheck regions. When
 * Unchecked, the Cartographic Boundary while being drawn didn't consider this region, so the region
 * is not drawn.
 *
 * @param viewModel the corresponding viewModel that provides access to data and methods for
 * manipulating the screen.
 * @param onSetToolbarInfo a function to set information for the parent composable's toolbar.
 */
@Composable
fun RegionsFromCartographicBoundaryScreen(
    viewModel: RegionsFromCartographicBoundaryViewModel = hiltViewModel(),
    onSetToolbarInfo: (screenInfo: ScreenInfo) -> Unit
) {
    val cartographicBoundary by viewModel
        .currentCartographicBoundaryFlow
        .collectAsState(initial = null)
    val regions = cartographicBoundary?.regions ?: emptyList()
    val administrativeUnitName = cartographicBoundary?.administrativeUnitName

    onSetToolbarInfo(
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