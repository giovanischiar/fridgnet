package io.schiar.fridgnet.view.administrationunits.component

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import io.schiar.fridgnet.view.shared.component.AdministrativeUnitView
import io.schiar.fridgnet.view.shared.viewdata.AdministrativeUnitViewData

/**
 * The grid of administrative units. Each unit is displayed as a Google Maps component with its
 * geographical outline (cartographic boundary) plotted, if available.
 *
 * @param administrativeUnits the administrative units view data to be considered.
 * @param columnCount the number of columns to lay out the administrative units. This value is also
 * used to calculate the size of each unit within the grid.
 * @param onAdministrativeUnitPressedAt event triggered when a administrative unit is pressed. It
 * contains its index as a param.
 */
@Composable
fun AdministrativeUnitsGrid(
    administrativeUnits: List<AdministrativeUnitViewData>,
    columnCount: Int,
    onAdministrativeUnitPressedAt: (index: Int) -> Unit
) {
    LazyVerticalGrid(columns = GridCells.Fixed(columnCount)) {
        items(count = administrativeUnits.size) { index ->
            val viewSize = 100f * 4 / columnCount
            val viewSizeInDP = Dp(value = viewSize)
            AdministrativeUnitView(
                modifier = Modifier.size(viewSizeInDP),
                administrativeUnit = administrativeUnits[index],
                areImagesShowing = false
            ) {
                onAdministrativeUnitPressedAt(index)
            }
        }
    }
}