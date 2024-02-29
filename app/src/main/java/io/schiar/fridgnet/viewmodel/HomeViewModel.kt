package io.schiar.fridgnet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.schiar.fridgnet.Log
import io.schiar.fridgnet.model.AdministrativeUnit
import io.schiar.fridgnet.model.AdministrativeUnit.valueOf
import io.schiar.fridgnet.model.repository.HomeRepository
import io.schiar.fridgnet.view.viewdata.AddressLocationImagesViewData
import io.schiar.fridgnet.viewmodel.util.toAddressLocationImagesViewDataList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(private val homeRepository: HomeRepository) : ViewModel() {
    private val _addressLocationImages = MutableStateFlow<List<AddressLocationImagesViewData>>(
        emptyList()
    )
    val addressLocationImages: StateFlow<List<AddressLocationImagesViewData>> =
        _addressLocationImages.asStateFlow()

    val administrativeUnits: StateFlow<List<String>> = MutableStateFlow(
        AdministrativeUnit.values().map { it.toString() }.toList()
    )

    private val _currentAdministrativeUnit = MutableStateFlow(
        AdministrativeUnit.CITY.toString()
    )
    val currentAdministrativeUnit: StateFlow<String> = _currentAdministrativeUnit

    fun subscribe() {
        homeRepository.subscribeForNewAddressAdded(callback = ::onAddressReady)
        homeRepository.subscribeForLocationsReady(callback = ::onLocationReady)
    }

    fun selectImages(address: String) = viewModelScope.launch {
        Log.d("Select Image Feature", "Select $address")
        homeRepository.selectImagesFrom(addressName = address)
    }

    fun changeCurrent(administrativeUnitName: String) {
        _currentAdministrativeUnit.update { administrativeUnitName }
        homeRepository.changeCurrent(administrativeUnit = valueOf(administrativeUnitName))
        onLocationReady()
    }

    fun removeAllImages() = viewModelScope.launch {
        homeRepository.removeAllImages()
    }

    private fun onAddressReady() {
        _addressLocationImages.update {
            homeRepository.locationImages().toAddressLocationImagesViewDataList()
        }
    }

    private fun onLocationReady() {
        _addressLocationImages.update {
            homeRepository.locationImages().toAddressLocationImagesViewDataList()
        }
    }
}