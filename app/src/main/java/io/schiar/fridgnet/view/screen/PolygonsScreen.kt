package io.schiar.fridgnet.view.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.*
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
import io.schiar.fridgnet.view.viewdata.RegionViewData
import io.schiar.fridgnet.viewmodel.PolygonsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

@Composable
fun PolygonsScreen(viewModel: PolygonsViewModel, info: (screenInfo: ScreenInfo) -> Unit) {
    val location by viewModel.currentLocation.collectAsState()
    val sortedRegions = (location ?: return).regions.sortedBy {
        it.polygon.coordinates.size
    }.asReversed()

    fun switchAllOtherRegions() = runBlocking {
        withContext(Dispatchers.IO) { viewModel.switchAll() }
    }

    fun switchRegion(regionViewData: RegionViewData) = runBlocking {
        withContext(Dispatchers.IO) { viewModel.switchRegion(regionViewData = regionViewData) }
    }

    info(
        ScreenInfo(
            title = location?.address ?: stringResource(id = R.string.polygons_screen),
            actions = {
                if (sortedRegions.size > 1) {
                    Button(
                        colors = buttonColors(containerColor = Color.Transparent),
                        onClick = ::switchAllOtherRegions) {
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
                        onRegionCheckedChange = ::switchRegion
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
                                onRegionCheckedChange = ::switchRegion
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
                                    onRegionCheckedChange = ::switchRegion
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
                                    onRegionCheckedChange = ::switchRegion
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}