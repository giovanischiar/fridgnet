package io.schiar.fridgnet.view.administrationunit

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.maps.android.compose.MapUiSettings
import io.schiar.fridgnet.view.administrationunit.component.PhotoGrid
import io.schiar.fridgnet.view.home.util.ScreenInfo
import io.schiar.fridgnet.view.shared.component.AdministrativeUnitView
import io.schiar.fridgnet.viewmodel.AdministrativeUnitViewModel

@Composable
fun AdministrativeUnitScreen(
    viewModel: AdministrativeUnitViewModel = hiltViewModel(),
    info: (screenInfo: ScreenInfo) -> Unit
) {
    val optionalAdministrativeUnit by viewModel.administrativeUnitFlow.collectAsState(
        initial = null
    )
    val administrativeUnit = optionalAdministrativeUnit ?: return
    val (name, _, _, _, images, _) = administrativeUnit
    val weight = remember { 0.65f }
    info(ScreenInfo(title = name))
    Column {
        AdministrativeUnitView(
            modifier = Modifier.fillMaxWidth().weight(weight),
            administrativeUnit = administrativeUnit,
            mapUISettings = MapUiSettings(zoomControlsEnabled = false),
            areImagesSizeShowing = false,
            areZoomControlsEnabled = true
        )
        PhotoGrid(modifier = Modifier.weight(1 - weight), images = images)
    }
}