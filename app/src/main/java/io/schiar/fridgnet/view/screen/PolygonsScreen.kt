package io.schiar.fridgnet.view.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.schiar.fridgnet.view.component.MapPolygon
import io.schiar.fridgnet.viewmodel.MainViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PolygonsScreen(viewModel: MainViewModel) {
    val location by viewModel.currentLocation.collectAsState()
    val sortedRegions = (location ?: return).regions.sortedBy {
        it.polygon.coordinates.size
    }.asReversed()

    LazyVerticalGrid(columns = GridCells.Fixed(4)) {
        item(span = { GridItemSpan(this.maxLineSpan) }) {
            Row {
                MapPolygon(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(350.dp),
                    region = sortedRegions[0]
                )

                if (sortedRegions.size == 2) {
                    MapPolygon(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(350.dp),
                        region = sortedRegions[1]
                    )
                }
            }
        }

        if (sortedRegions.size > 3) {
            item(span = { GridItemSpan(this.maxLineSpan) }) {
                FlowRow(maxItemsInEachRow = 2) {
                    sortedRegions.subList(1, 3).map { region ->
                        MapPolygon(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .height(250.dp),
                            region = region
                        )
                    }
                }
            }
        }

        val list = sortedRegions.subList(4, sortedRegions.size)
        items(list.size) { index ->
            val region = list[index]
            Row {
                MapPolygon(
                    modifier = Modifier.height(75.dp),
                    region = region
                )
            }
        }
    }
}