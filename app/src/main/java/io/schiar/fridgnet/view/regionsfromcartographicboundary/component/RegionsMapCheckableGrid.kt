package io.schiar.fridgnet.view.regionsfromcartographicboundary.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import io.schiar.fridgnet.view.shared.viewdata.RegionViewData

/**
 * The component that displays a personalized grid of `RegionMapCheckableView`s. It positions the
 * main region on top and the others below, with sizes based on the number of regions displayed.
 *
 * @param regions the list of region data objects to be displayed.
 * @param onRegionCheckedChangeAt the event fired when a region's checkbox is pressed. It can be
 * called with either the region data object or its index within the list.
 */
@Composable
fun RegionsMapCheckableGrid(
    regions: List<RegionViewData>,
    onRegionCheckedChangeAt: (index: Int) -> Unit)
{
    val sortedRegions = regions.sortedBy { it.polygon.geoLocations.size }.asReversed()
    val configuration = LocalConfiguration.current
    val localDensity = LocalDensity.current
    var height by remember { mutableStateOf(configuration.screenHeightDp.dp) }

    fun handleRegionChecked(region: RegionViewData) {
        val index = regions.indexOf(region)
        onRegionCheckedChangeAt(index)
    }

    Box(modifier = Modifier.onGloballyPositioned { coordinates ->
        height = with(localDensity) { coordinates.size.height.toDp() }
    }) {
        when (sortedRegions.size) {
            0 -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            1 -> {
                RegionMapCheckableView(
                    modifier = Modifier.fillMaxSize(),
                    region = sortedRegions[0]
                )
            }

            2 -> {
                Column {
                    RegionMapCheckableView(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(0.75f),
                        region = sortedRegions[0]
                    )

                    RegionMapCheckableView(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(0.25f),
                        region = sortedRegions[1],
                        onRegionCheckedChangeAt = ::handleRegionChecked
                    )
                }
            }

            3 -> {
                Column {
                    RegionMapCheckableView(
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
                            RegionMapCheckableView(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                region = region,
                                onRegionCheckedChangeAt = ::handleRegionChecked
                            )
                        }
                    }
                }
            }

            else -> {
                LazyVerticalGrid(columns = GridCells.Fixed(count = 4)) {
                    item(span = { GridItemSpan(this.maxLineSpan) }) {
                        RegionMapCheckableView(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(height * 0.75f),
                            region = sortedRegions[0]
                        )
                    }

                    item(span = { GridItemSpan(this.maxLineSpan) }) {
                        Row {
                            sortedRegions.subList(1, 3).map { region ->
                                RegionMapCheckableView(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f)
                                        .height(height * 0.25f),
                                    region = region,
                                    onRegionCheckedChangeAt = ::handleRegionChecked
                                )
                            }
                        }
                    }

                    if (sortedRegions.size > 3) {
                        val list = sortedRegions.subList(4, sortedRegions.size)
                        items(list.size) { index ->
                            val region = list[index]
                            Row {
                                RegionMapCheckableView(
                                    modifier = Modifier.height(height * 0.125f),
                                    region = region,
                                    onRegionCheckedChangeAt = ::handleRegionChecked
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}