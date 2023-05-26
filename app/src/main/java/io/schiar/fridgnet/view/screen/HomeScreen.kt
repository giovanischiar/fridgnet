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
import io.schiar.fridgnet.view.component.MapPhotoItem
import io.schiar.fridgnet.view.util.ScreenInfo
import io.schiar.fridgnet.viewmodel.MainViewModel

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    onNavigateImage: () -> Unit,
    info: (screenInfo: ScreenInfo) -> Unit
) {
    info(
        ScreenInfo(title = stringResource(id = R.string.home_screen))
    )

    LaunchedEffect(Unit) { viewModel.subscribeLocationRepository() }
    val imagesWithLocation by viewModel.imagesWithLocation.collectAsState()
    val allLocationAddress by viewModel.allLocationAddress.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LazyVerticalGrid(columns = GridCells.Fixed(4)) {
            imagesWithLocation.entries.map { (address, images) ->
                item {
                    MapPhotoItem(
                        initialLocation = images[0].coordinate,
                        location = allLocationAddress[address]
                    ) {
                        viewModel.selectImages(address)
                        onNavigateImage()
                    }
                }
            }
        }
    }
}
