package io.schiar.fridgnet.viewmodel

import androidx.lifecycle.ViewModel
import io.schiar.fridgnet.model.repository.Repository
import io.schiar.fridgnet.view.viewdata.AddressLocationImagesViewData
import io.schiar.fridgnet.view.viewdata.ImageViewData
import io.schiar.fridgnet.viewmodel.util.toAddressLocationImagesViewDataList
import io.schiar.fridgnet.viewmodel.util.toImageViewDataList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class HomeViewModel(private val repository: Repository): ViewModel() {
    private val _addressLocationImages = MutableStateFlow<List<AddressLocationImagesViewData>>(
        emptyList()
    )
    val addressLocationImages: StateFlow<List<AddressLocationImagesViewData>> =
        _addressLocationImages.asStateFlow()

    private val _selectedImages = MutableStateFlow<Pair<String, List<ImageViewData>>>(
        value = Pair("", emptyList())
    )
    val selectedImages: StateFlow<Pair<String, List<ImageViewData>>> = _selectedImages.asStateFlow()

    fun subscribe() {
        repository.subscribeForAddressImageAdded(callback = ::onAddressReady)
        repository.subscribeForLocationsReady(callback = ::onLocationReady)
    }

    fun selectImages(address: String) {
        val images = repository.selectImagesFrom(addressName = address) ?: return
        _selectedImages.update { address to images.toImageViewDataList() }
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