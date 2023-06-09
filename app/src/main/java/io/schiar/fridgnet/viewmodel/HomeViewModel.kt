package io.schiar.fridgnet.viewmodel

import androidx.lifecycle.ViewModel
import io.schiar.fridgnet.model.repository.HomeRepository
import io.schiar.fridgnet.view.viewdata.AddressLocationImagesViewData
import io.schiar.fridgnet.viewmodel.util.toAddressLocationImagesViewDataList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class HomeViewModel(private val repository: HomeRepository): ViewModel() {
    private val _addressLocationImages = MutableStateFlow<List<AddressLocationImagesViewData>>(
        emptyList()
    )
    val addressLocationImages: StateFlow<List<AddressLocationImagesViewData>> =
        _addressLocationImages.asStateFlow()

    fun subscribe() {
        repository.subscribeForAddressImageAdded(callback = ::onAddressReady)
        repository.subscribeForLocationsReady(callback = ::onLocationReady)
    }

    fun selectImages(address: String) {
        repository.selectImagesFrom(addressName = address)
    }

    private fun onAddressReady() {
        _addressLocationImages.update {
            repository.locationImages().toAddressLocationImagesViewDataList()
        }
    }

    private fun onLocationReady() {
        _addressLocationImages.update {
            repository.locationImages().toAddressLocationImagesViewDataList()
        }
    }
}