package io.schiar.fridgnet.view.screen.administrationunits

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import io.schiar.fridgnet.R
import io.schiar.fridgnet.view.screen.administrationunits.component.AdministrativeLevelDropdown
import io.schiar.fridgnet.view.screen.administrationunits.component.AdministrativeUnitsGrid
import io.schiar.fridgnet.view.screen.administrationunits.component.ToolbarMenuItems
import io.schiar.fridgnet.view.util.ScreenInfo
import io.schiar.fridgnet.viewmodel.AdministrativeUnitsViewModel

@Composable
fun AdministrativeUnitsScreen(
    viewModel: AdministrativeUnitsViewModel,
    onNavigateImage: () -> Unit,
    info: (screenInfo: ScreenInfo) -> Unit
) {
    val administrativeLevels by viewModel.administrativeLevelsFlow
        .collectAsState(initial = emptyList())
    val optionalCurrentAdministrativeLevel by viewModel.currentAdministrativeLevelFlow
        .collectAsState(initial = null)
    val administrativeUnits by viewModel.administrativeUnitsFlow
        .collectAsState(initial = emptyList())
    val currentAdministrativeLevel = optionalCurrentAdministrativeLevel ?: return

    info(
        ScreenInfo(
            title = stringResource(id = R.string.home_screen),
            actions = {
                AdministrativeLevelDropdown(
                    administrativeLevels = administrativeLevels,
                    currentAdministrativeLevel = currentAdministrativeLevel,
                    onDropdown = viewModel::changeCurrentAdministrativeLevel
                )

                ToolbarMenuItems(
                    itemTitleIDs = listOf(R.string.remove_all_images),
                    onItemPressedAt = { viewModel.removeAllImages() }
                )
            }
        )
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AdministrativeUnitsGrid(
            administrativeUnits = administrativeUnits,
            columnCount = currentAdministrativeLevel.columnCount,
            onAdministrativeUnitPressedAt = { index ->
                viewModel.selectCartographicBoundaryGeoLocationAt(index)
                onNavigateImage()
            }
        )
    }
}
