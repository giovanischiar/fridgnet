package io.schiar.fridgnet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import io.schiar.fridgnet.view.screen.AppScreen
import io.schiar.fridgnet.viewmodel.AppViewModel
import io.schiar.fridgnet.viewmodel.HomeViewModel
import io.schiar.fridgnet.viewmodel.MapViewModel
import io.schiar.fridgnet.viewmodel.PhotosViewModel
import io.schiar.fridgnet.viewmodel.PolygonsViewModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val appViewModel: AppViewModel by viewModels()
    private val homeViewModel: HomeViewModel by viewModels()
    private val mapViewModel: MapViewModel by viewModels()
    private val polygonsViewModel: PolygonsViewModel by viewModels()
    private val photosViewModel: PhotosViewModel by viewModels()

    override fun onCreate(saveBundleInstance: Bundle?) {
        super.onCreate(saveBundleInstance)
        Log.fromAndroid = true
        setContent {
            AppScreen(
                appViewModel = appViewModel,
                homeViewModel = homeViewModel,
                mapViewModel = mapViewModel,
                polygonsViewModel = polygonsViewModel,
                photosViewModel = photosViewModel
            )
        }
    }
}