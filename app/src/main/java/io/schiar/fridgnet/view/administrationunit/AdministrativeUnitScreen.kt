package io.schiar.fridgnet.view.administrationunit

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.google.maps.android.compose.MapUiSettings
import io.schiar.fridgnet.view.administrationunit.component.PhotoGrid
import io.schiar.fridgnet.view.home.util.ScreenInfo
import io.schiar.fridgnet.view.shared.component.AdministrativeUnitView
import io.schiar.fridgnet.view.shared.component.Loading
import io.schiar.fridgnet.view.shared.viewdata.AdministrativeUnitViewData

/**
 * The composable representing the Administrative Unit Screen. It displays a Google Map component
 * with all images pinned to their locations, along with a grid of all images ordered by date below
 * the map.
 *
 * @param uiState The current UI state that provides access to administrative unit data
 * and encapsulates the possible states of the screen (loading, loaded).
 * @param onSetToolbarInfo a function to set information for the parent composable's toolbar,
 * such as title and components.
 */
@Composable
fun AdministrativeUnitScreen(
    uiState: AdministrativeUnitUiState,
    onSetToolbarInfo: (screenInfo: ScreenInfo) -> Unit
) {
    when (uiState) {
        is AdministrativeUnitUiState.Loading -> Loading()

        is AdministrativeUnitUiState.AdministrativeUnitLoaded -> {
            AdministrativeUnitScreenLoaded(
                administrativeUnit = uiState.administrativeUnit,
                onSetToolbarInfo = onSetToolbarInfo
            )
        }
    }
}

@Composable
private fun AdministrativeUnitScreenLoaded(
    administrativeUnit: AdministrativeUnitViewData,
    onSetToolbarInfo: (screenInfo: ScreenInfo) -> Unit
) {
    val (name, _, _, _, images, _) = administrativeUnit
    val weight = remember { 0.65f }
    onSetToolbarInfo(ScreenInfo(title = name))
    Column {
        AdministrativeUnitView(
            modifier = Modifier
                .fillMaxWidth()
                .weight(weight),
            administrativeUnit = administrativeUnit,
            mapUISettings = MapUiSettings(zoomControlsEnabled = false),
            areImagesSizeShowing = false,
            areZoomControlsEnabled = true
        )
        PhotoGrid(modifier = Modifier.weight(1 - weight), images = images)
    }
}