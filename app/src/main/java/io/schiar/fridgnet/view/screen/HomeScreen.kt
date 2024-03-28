package io.schiar.fridgnet.view.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.DropdownMenu
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import io.schiar.fridgnet.R
import io.schiar.fridgnet.view.component.AdministrativeLevelDropdown
import io.schiar.fridgnet.view.component.AdministrativeUnitView
import io.schiar.fridgnet.view.util.ScreenInfo
import io.schiar.fridgnet.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateImage: () -> Unit,
    info: (screenInfo: ScreenInfo) -> Unit
) {
    val administrativeLevels by viewModel.administrativeLevelsFlow.collectAsState(initial = emptyList())
    val currentAdministrativeLevel by viewModel.currentAdministrativeLevelFlow.collectAsState(
        initial = ""
    )

    var expanded by remember { mutableStateOf(false) }

    info(
        ScreenInfo(
            title = stringResource(id = R.string.home_screen),
            actions = {
                AdministrativeLevelDropdown(
                    administrativeLevels = administrativeLevels,
                    currentAdministrativeLevel = currentAdministrativeLevel,
                    onDropdown = viewModel::changeCurrentAdministrativeLevel
                )

                Box {
                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(
                            imageVector = Icons.Outlined.MoreVert,
                            contentDescription = "More",
                            tint = colorResource(id = R.color.white)
                        )
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false })
                    {
                        DropdownMenuItem(
                            text = { Text("Remove All Images") },
                            onClick = {
                                viewModel.removeAllImages()
                                expanded = false
                            }
                        )
                    }
                }
            }
        )
    )

    val administrativeUnits by viewModel.administrativeUnitsFlow.collectAsState(initial = emptyList())

    fun getColumnSize(): Int {
        return when (currentAdministrativeLevel) {
            "CITY" -> 4
            "COUNTY" -> 3
            "STATE" -> 3
            "COUNTRY" -> 1
            else -> 4
        }
    }

    val columnCount = getColumnSize()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LazyVerticalGrid(columns = GridCells.Fixed(columnCount)) {
            items(count = administrativeUnits.size) { index ->
                AdministrativeUnitView(
                    modifier = Modifier.size(Dp(100f * 4 / columnCount)),
                    administrativeUnit = administrativeUnits[index],
                    areImagesShowing = false
                ) {
                    viewModel.selectCartographicBoundaryGeoLocationAt(index)
                    onNavigateImage()
                }
            }
        }
    }
}
