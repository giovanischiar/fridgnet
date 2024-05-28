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
import io.schiar.fridgnet.R
import io.schiar.fridgnet.view.administrationunits.component.AdministrativeLevelDropdown
import io.schiar.fridgnet.view.administrationunits.component.AdministrativeUnitsGrid
import io.schiar.fridgnet.view.administrationunits.component.ToolbarMenuItems
import io.schiar.fridgnet.view.home.util.ScreenInfo
import io.schiar.fridgnet.viewmodel.AdministrativeUnitsViewModel

/**
 * The component that represents the Administrative Units Screen. It displays a grid of Google Maps
 * components, each representing an administrative unit. The map displays the unit's geographic
 * boundary (if available) and the number of images in the upper right corner.
 *
 * @param viewModel the corresponding viewModel that provide access of all necessary data to populate
 * the screen and methods to manipulate it.
 * @param onNavigateToAdministrativeUnit when the user selects a Google Maps Component (the
 * administrative unit) this method is called to redirect the user to the Administrative Unit Unit
 * screen.
 * @param onSetToolbarInfo a function to set information for the parent composable's toolbar,
 * such as title, actions menu, and potentially other elements.
 */
@Composable
fun AdministrativeUnitsScreen(
    viewModel: AdministrativeUnitsViewModel,
    onNavigateToAdministrativeUnit: () -> Unit,
    onSetToolbarInfo: (screenInfo: ScreenInfo) -> Unit
) {
    val administrativeLevels by viewModel.administrativeLevelsFlow
        .collectAsState(initial = emptyList())
    val optionalCurrentAdministrativeLevel by viewModel.currentAdministrativeLevelFlow
        .collectAsState(initial = null)
    val administrativeUnits by viewModel.administrativeUnitsFlow
        .collectAsState(initial = emptyList())
    val currentAdministrativeLevel = optionalCurrentAdministrativeLevel ?: return

    onSetToolbarInfo(
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
