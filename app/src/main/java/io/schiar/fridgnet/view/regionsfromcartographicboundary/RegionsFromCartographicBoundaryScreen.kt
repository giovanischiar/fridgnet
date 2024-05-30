package io.schiar.fridgnet.view.regionsfromcartographicboundary

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toUpperCase
import io.schiar.fridgnet.R
import io.schiar.fridgnet.view.home.util.ScreenInfo
import io.schiar.fridgnet.view.regionsfromcartographicboundary.component.RegionsMapCheckableGrid
import io.schiar.fridgnet.view.regionsfromcartographicboundary.uiState.CartographicBoundaryUiState
import io.schiar.fridgnet.view.shared.component.Loading

/**
 * The component representing the Regions From Cartographic Boundary Screen. It displays all regions
 * that belong to a single Cartographic Boundary. It allows checking or unchecking regions. When
 * unchecked, the Cartographic Boundary, while being drawn, doesn't consider this region, so the
 * region is not drawn.
 *
 * @param cartographicBoundaryUiState The UI state for the cartographic boundary, which determines
 * whether the data is loading or loaded with the cartographic boundary and its regions.
 * @param switchAll A callback function to switch the state of all regions (check or uncheck all).
 * @param switchRegionAt A callback function to switch the state of a specific region at the given
 * index.
 * @param onChangeToolbarInfo A function to set information for the parent composable's toolbar.
 */
@Composable
fun RegionsFromCartographicBoundaryScreen(
    cartographicBoundaryUiState: CartographicBoundaryUiState,
    switchAll: () -> Unit,
    switchRegionAt: (index: Int) -> Unit,
    onChangeToolbarInfo: (screenInfo: ScreenInfo) -> Unit
) {
    val regionsSize = when (cartographicBoundaryUiState) {
        is CartographicBoundaryUiState.Loading -> 1
        is CartographicBoundaryUiState.CartographicBoundaryLoaded -> {
            cartographicBoundaryUiState.cartographicBoundary.regions.size
        }
    }

    val administrativeUnitName = when (cartographicBoundaryUiState) {
        is CartographicBoundaryUiState.Loading -> stringResource(id = R.string.polygons_screen)
        is CartographicBoundaryUiState.CartographicBoundaryLoaded -> {
            cartographicBoundaryUiState.cartographicBoundary.administrativeUnitName
        }
    }

    onChangeToolbarInfo(
        ScreenInfo(
            title = administrativeUnitName,
            actions = {
                if (regionsSize > 1) {
                    Button(
                        colors = buttonColors(containerColor = Color.Transparent),
                        onClick = switchAll
                    ) {
                        Text(stringResource(id = R.string.switch_all).toUpperCase(Locale.current))
                    }
                }
            }
        )
    )

    when (cartographicBoundaryUiState) {
        is CartographicBoundaryUiState.Loading -> Loading()
        is CartographicBoundaryUiState.CartographicBoundaryLoaded -> {
            RegionsMapCheckableGrid(
                regions = cartographicBoundaryUiState.cartographicBoundary.regions,
                onRegionCheckedChangeAt = switchRegionAt
            )
        }
    }
}