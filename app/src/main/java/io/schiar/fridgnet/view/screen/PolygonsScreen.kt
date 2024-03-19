package io.schiar.fridgnet.view.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.schiar.fridgnet.R
import io.schiar.fridgnet.view.component.MapPolygon
import io.schiar.fridgnet.view.util.ScreenInfo
import io.schiar.fridgnet.viewmodel.PolygonsViewModel

@Composable
fun PolygonsScreen(viewModel: PolygonsViewModel, info: (screenInfo: ScreenInfo) -> Unit) {
    val location by viewModel.currentLocation.collectAsState(initial = null)
    val regions = (location ?: return).regions
    val sortedRegions = regions.sortedBy {
        it.polygon.geoLocations.size
    }.asReversed()

    info(
        ScreenInfo(
            title = location?.address ?: stringResource(id = R.string.polygons_screen),
            actions = {
                if (sortedRegions.size > 1) {
                    Button(
                        colors = buttonColors(containerColor = Color.Transparent),
                        onClick = viewModel::switchAll
                    ) {
                        Text("SWITCH ALL")
                    }
                }
            }
        )
    )

    val configuration = LocalConfiguration.current
    val localDensity = LocalDensity.current
    var height by remember { mutableStateOf(configuration.screenHeightDp.dp) }

    Box(modifier = Modifier.onGloballyPositioned { coordinates ->
        height = with(localDensity) { coordinates.size.height.toDp() }
    }) {
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
                        region = sortedRegions[1],
                        onRegionCheckedChangeAt = {
                            viewModel.switchRegionAt(index = regions.indexOf(
                                element = it
                            ))
                        }
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

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(0.25f)
                    ) {
                        sortedRegions.subList(1, 3).map { region ->
                            MapPolygon(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                region = region,
                                onRegionCheckedChangeAt = {
                                    viewModel.switchRegionAt(index = regions.indexOf(
                                        element = it
                                    ))
                                }
                            )
                        }
                    }
                }
            }

            else -> {
                LazyVerticalGrid(columns = GridCells.Fixed(4)) {
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
                                    region = region,
                                    onRegionCheckedChangeAt = {
                                        viewModel.switchRegionAt(index = regions.indexOf(
                                            element = it
                                        ))
                                    }
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
                                    region = region,
                                    onRegionCheckedChangeAt = {
                                        viewModel.switchRegionAt(index = regions.indexOf(
                                            element = it
                                        ))
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}