package io.schiar.fridgnet.viewmodel.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.schiar.fridgnet.model.repository.address.AddressRepository
import io.schiar.fridgnet.model.repository.location.LocationRepository
import io.schiar.fridgnet.viewmodel.MainViewModel

class MainViewModelFactory(
    private val locationRepository: LocationRepository,
    private val addressRepository: AddressRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when (modelClass) {
            MainViewModel::class.java -> MainViewModel(
                locationRepository = locationRepository,
                addressRepository = addressRepository
            )
            else -> throw IllegalArgumentException("Unknown view model class: ${modelClass.name}")
        } as T
    }
}