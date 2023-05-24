package io.schiar.fridgnet.view.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import io.schiar.fridgnet.view.component.MapPolygon
import io.schiar.fridgnet.viewmodel.MainViewModel

@Composable
fun PolygonsScreen(viewModel: MainViewModel) {
    val configuration = LocalConfiguration.current
    val location by viewModel.currentLocation.collectAsState()
    val sortedRegions = (location ?: return).regions.sortedBy {
        it.polygon.coordinates.size
    }.asReversed()

    val localDensity = LocalDensity.current
    var height by remember { mutableStateOf(configuration.screenHeightDp.dp) }

    when (sortedRegions.size) {
        1 -> {
            MapPolygon(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                region = sortedRegions[0]
            )
        }

        2 -> {
            Column {
                MapPolygon(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.75f),
                    region = sortedRegions[0]
                )

                MapPolygon(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.25f),
                    region = sortedRegions[1]
                )
            }
        }

        3 -> {
            Column {
                MapPolygon(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.75f),
                    region = sortedRegions[0]
                )

                Row(modifier = Modifier.fillMaxWidth().weight(0.25f)) {
                    sortedRegions.subList(1, 3).map { region ->
                        MapPolygon(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            region = region
                        )
                    }
                }
            }
        }

        else -> {
            LazyVerticalGrid(
                modifier = Modifier.onGloballyPositioned { coordinates ->
                    height = with(localDensity) { coordinates.size.height.toDp() }
                },
                columns = GridCells.Fixed(4)) {
                item(span = { GridItemSpan(this.maxLineSpan) }) {
                    MapPolygon(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(height * 0.75f),
                        region = sortedRegions[0]
                    )
                }

                item(span = { GridItemSpan(this.maxLineSpan) }) {
                    Row {
                        sortedRegions.subList(1, 3).map { region ->
                            MapPolygon(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .height(height * 0.25f),
                                region = region
                            )
                        }
                    }
                }

                if (sortedRegions.size > 3) {
                    val list = sortedRegions.subList(4, sortedRegions.size)
                    items(list.size) { index ->
                        val region = list[index]
                        Row {
                            MapPolygon(
                                modifier = Modifier.height(height * 0.125f),
                                region = region
                            )
                        }
                    }
                }
            }
        }
    }
}