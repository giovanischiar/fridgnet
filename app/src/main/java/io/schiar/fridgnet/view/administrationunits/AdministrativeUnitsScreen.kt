package io.schiar.fridgnet.view.administrationunits

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import io.schiar.fridgnet.R
import io.schiar.fridgnet.view.administrationunits.component.AdministrativeLevelDropdown
import io.schiar.fridgnet.view.administrationunits.component.AdministrativeUnitsGrid
import io.schiar.fridgnet.view.administrationunits.component.ToolbarMenuItems
import io.schiar.fridgnet.view.administrationunits.uistate.AdministrativeLevelsUiState
import io.schiar.fridgnet.view.administrationunits.uistate.AdministrativeUnitsUiState
import io.schiar.fridgnet.view.administrationunits.uistate.CurrentAdministrativeLevelUiState
import io.schiar.fridgnet.view.home.util.ScreenInfo

/**
 * The component that represents the Administrative Units Screen. It displays a grid of Google Maps
 * components, each representing an administrative unit. The map displays the unit's geographic
 * boundary (if available) and the number of images in the upper right corner.
 *
 * @param administrativeLevelsUiState A ui state list of available administrative levels for
 * navigation.
 * @param currentAdministrativeLevelUiState The ui state of current administrative level being
 * displayed.
 * @param administrativeUnitsUiState The UI state for administrative units, which determines whether
 * the data is loading or loaded.
 * @param onDropdownSelectedAt A callback function to be invoked when an item is selected from the
 * dropdown.
 * @param onNavigateToAdministrativeUnit A callback function to be invoked when navigating to an
 * administrative unit.
 * @param onRemoveAllImagesButtonPressed A callback function to be invoked when the "Remove All
 * Images" button is pressed.
 * @param onAdministrativeUnitPressedAt A callback function to be invoked when an administrative
 * unit is pressed.
 * @param onSetToolbarInfo A callback function to set the toolbar information with the provided
 * screen info.
 */
@Composable
fun AdministrativeUnitsScreen(
    administrativeLevelsUiState: AdministrativeLevelsUiState,
    administrativeUnitsUiState: AdministrativeUnitsUiState,
    currentAdministrativeLevelUiState: CurrentAdministrativeLevelUiState,
    onDropdownSelectedAt: (index: Int) -> Unit,
    onNavigateToAdministrativeUnit: () -> Unit,
    onRemoveAllImagesButtonPressed: () -> Unit,
    onAdministrativeUnitPressedAt: (index: Int) -> Unit,
    onSetToolbarInfo: (screenInfo: ScreenInfo) -> Unit
) {
    onSetToolbarInfo(
        ScreenInfo(
            title = stringResource(id = R.string.administrative_units_screen),
            actions = {
                AdministrativeLevelDropdown(
                    administrativeLevelsUiState = administrativeLevelsUiState,
                    currentAdministrativeLevelUiState = currentAdministrativeLevelUiState,
                    onDropdown = onDropdownSelectedAt
                )

                ToolbarMenuItems(
                    itemTitleIDs = listOf(R.string.remove_all_images),
                    onItemPressedAt = { onRemoveAllImagesButtonPressed() }
                )
            }
        )
    )

    val columnCount = if (
        currentAdministrativeLevelUiState is CurrentAdministrativeLevelUiState
        .CurrentAdministrativeLevelLoaded
    ) currentAdministrativeLevelUiState.currentAdministrativeLevel.columnCount else 4

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (administrativeUnitsUiState) {
            is AdministrativeUnitsUiState.Loading -> CircularProgressIndicator()
            is AdministrativeUnitsUiState.AdministrativeUnitsLoaded -> {
                AdministrativeUnitsGrid(
                    administrativeUnits = administrativeUnitsUiState.administrativeUnits,
                    columnCount = columnCount,
                    onAdministrativeUnitPressedAt = { index ->
                        onAdministrativeUnitPressedAt(index)
                        onNavigateToAdministrativeUnit()
                    }
                )
            }
        }
    }
}
