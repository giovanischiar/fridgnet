package io.schiar.fridgnet.view.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import io.schiar.fridgnet.R
import io.schiar.fridgnet.view.component.AdministrativeUnitDropdown
import io.schiar.fridgnet.view.component.MapPhotoItem
import io.schiar.fridgnet.view.util.ScreenInfo
import io.schiar.fridgnet.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateImage: () -> Unit,
    info: (screenInfo: ScreenInfo) -> Unit
) {
    val administrativeUnits by viewModel.administrativeUnits.collectAsState()
    val currentAdministrativeUnit by viewModel.currentAdministrativeUnit.collectAsState()

    info(
        ScreenInfo(
            title = stringResource(id = R.string.home_screen),
            actions = {
                AdministrativeUnitDropdown(
                    administrativeUnits = administrativeUnits,
                    currentAdministrativeUnit = currentAdministrativeUnit
                ) {
                    viewModel.changeCurrent(administrativeUnitName = it)
                }
            }
        )
    )

    LaunchedEffect(Unit) { viewModel.subscribe() }

    val addressLocationImages by viewModel.addressLocationImages.collectAsState()

    fun getColumnSize(): Int {
        return when (currentAdministrativeUnit) {
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
            items(count = addressLocationImages.size) {
                val (address, location, initialCoordinate) = addressLocationImages[it]
                MapPhotoItem(
                    initialCoordinate = initialCoordinate,
                    location = location,
                    columnCount = columnCount
                ) {
                    viewModel.selectImages(address)
                    onNavigateImage()
                }
            }
        }
    }
}
