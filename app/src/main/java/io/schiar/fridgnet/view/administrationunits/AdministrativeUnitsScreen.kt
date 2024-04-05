package io.schiar.fridgnet.view.administrationunits

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import io.schiar.fridgnet.R
import io.schiar.fridgnet.view.administrationunits.component.AdministrativeLevelDropdown
import io.schiar.fridgnet.view.administrationunits.component.AdministrativeUnitsGrid
import io.schiar.fridgnet.view.administrationunits.component.ToolbarMenuItems
import io.schiar.fridgnet.view.home.util.ScreenInfo
import io.schiar.fridgnet.viewmodel.AdministrativeUnitsViewModel

@Composable
fun AdministrativeUnitsScreen(
    viewModel: AdministrativeUnitsViewModel = hiltViewModel(),
    onNavigateToAdministrativeUnit: () -> Unit,
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
            title = stringResource(id = R.string.administrative_units_screen),
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
                onNavigateToAdministrativeUnit()
            }
        )
    }
}
