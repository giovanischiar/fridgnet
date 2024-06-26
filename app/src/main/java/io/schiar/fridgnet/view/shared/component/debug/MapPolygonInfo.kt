package io.schiar.fridgnet.view.shared.component.debug

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.schiar.fridgnet.view.shared.viewdata.RegionViewData
import io.schiar.fridgnet.viewmodel.util.toBoundingBox

/**
 * Component used for debugging map rendering. It displays information about the visible
 * regions and identifies any polygons that cross the antimeridian (180th longitude).
 *
 * @param modifier the modifier used for the internal [Column].
 * @param visibleRegions the list of currently visible region data objects.
 */
@Composable
fun MapPolygonInfo(modifier: Modifier, visibleRegions: List<RegionViewData>) {
    Column(
        modifier = modifier
            .background(Color.White)
            .padding(PaddingValues(end = 5.dp))
    ) {
        Text(text = "Polygons now being drawn: ${visibleRegions.size} ")
        Text(
            text = "Polygons that contains antimeridian: ${
                visibleRegions.filter {
                    it.boundingBox.toBoundingBox().containsAntimeridian()
                }.size
            } "
        )
    }
}