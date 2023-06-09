package io.schiar.fridgnet.viewmodel.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.schiar.fridgnet.model.repository.AppRepository
import io.schiar.fridgnet.model.repository.MainRepository
import io.schiar.fridgnet.viewmodel.*

class ViewModelFactory(private val repository: MainRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when (modelClass) {
            AppViewModel::class.java -> AppViewModel(repository = repository)
            HomeViewModel::class.java -> HomeViewModel(repository = repository)
            MapViewModel::class.java -> MapViewModel(repository = repository)
            PolygonsViewModel::class.java -> PolygonsViewModel(repository = repository)
            PhotosViewModel::class.java -> PhotosViewModel(repository = repository)
            else -> throw IllegalArgumentException("Unknown view model class: ${modelClass.name}")
        } as T
    }
}