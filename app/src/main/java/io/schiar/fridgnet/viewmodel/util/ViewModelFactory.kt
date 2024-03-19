package io.schiar.fridgnet.viewmodel.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.schiar.fridgnet.model.repository.AppRepository
import io.schiar.fridgnet.model.repository.HomeRepository
import io.schiar.fridgnet.model.repository.MapRepository
import io.schiar.fridgnet.model.repository.PhotosRepository
import io.schiar.fridgnet.model.repository.PolygonsRepository
import io.schiar.fridgnet.viewmodel.AppViewModel
import io.schiar.fridgnet.viewmodel.HomeViewModel
import io.schiar.fridgnet.viewmodel.MapViewModel
import io.schiar.fridgnet.viewmodel.PhotosViewModel
import io.schiar.fridgnet.viewmodel.PolygonsViewModel

class ViewModelFactory(
    private val appRepository: AppRepository,
    private val homeRepository: HomeRepository,
    private val mapRepository: MapRepository,
    private val polygonsRepository: PolygonsRepository,
    private val photosRepository: PhotosRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when (modelClass) {
            AppViewModel::class.java -> AppViewModel(appRepository = appRepository)
            HomeViewModel::class.java -> HomeViewModel(homeRepository = homeRepository)
            MapViewModel::class.java -> MapViewModel(mapRepository = mapRepository)
            PolygonsViewModel::class.java -> {
                PolygonsViewModel(polygonsRepository = polygonsRepository)
            }
            PhotosViewModel::class.java -> PhotosViewModel(photosRepository = photosRepository)
            else -> throw IllegalArgumentException("Unknown view model class: ${modelClass.name}")
        } as T
    }
}