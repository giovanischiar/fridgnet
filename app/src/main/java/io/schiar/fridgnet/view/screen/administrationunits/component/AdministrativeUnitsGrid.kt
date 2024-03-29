package io.schiar.fridgnet.view.screen.administrationunits.component

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import io.schiar.fridgnet.view.component.AdministrativeUnitView
import io.schiar.fridgnet.view.viewdata.AdministrativeUnitViewData

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