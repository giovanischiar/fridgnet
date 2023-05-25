package io.schiar.fridgnet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.schiar.fridgnet.model.*
import io.schiar.fridgnet.model.repository.LocationRepository
import io.schiar.fridgnet.view.viewdata.BoundingBoxViewData
import io.schiar.fridgnet.view.viewdata.ImageViewData
import io.schiar.fridgnet.view.viewdata.LocationViewData
import io.schiar.fridgnet.view.viewdata.RegionViewData
import io.schiar.fridgnet.viewmodel.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel(
    private val locationRepository: LocationRepository = LocationRepository()
): ViewModel() {
    private var _images: Map<String, Image> = emptyMap()
    private var _addressImages: Map<String, List<Image>> = emptyMap()
    private var _locationAddress: Map<String, Location> = emptyMap()
    private var _regionLocation: Map<Region, Location> = emptyMap()

    private val _visibleImages = MutableStateFlow(value = _images.toImageViewData())
    val visibleImages: StateFlow<List<ImageViewData>> = _visibleImages.asStateFlow()

    private val _selectedImages = MutableStateFlow<List<ImageViewData>>(value = emptyList())
    val selectedImages: StateFlow<List<ImageViewData>> = _selectedImages.asStateFlow()

    private val _imageWithLocations = MutableStateFlow(
        value = _addressImages.toStringImageViewDataList()
    )
    val imagesWithLocation: StateFlow<Map<String, List<ImageViewData>>> =
        _imageWithLocations.asStateFlow()

    private val _allLocationAddress = MutableStateFlow(
        value = _locationAddress.toStringLocationViewData()
    )
    val allLocationAddress: StateFlow<Map<String, LocationViewData>> =
        _allLocationAddress.asStateFlow()

    private val _currentLocation = MutableStateFlow<LocationViewData?>(null)
    val currentLocation: StateFlow<LocationViewData?> = _currentLocation.asStateFlow()

    private val _visibleRegions: MutableStateFlow<List<RegionViewData>>
        = MutableStateFlow(emptyList())

    val visibleRegions: StateFlow<List<RegionViewData>> = _visibleRegions.asStateFlow()

    fun subscribeLocationRepository(
        callback: (regionLocation: Map<Region, Location>) -> Unit = this::onRegionLocationReady
    ) {
        locationRepository.subscribeOnRegionLocationReady(
            onRegionLocationReady = callback
        )
    }

    fun addImage(uri: String, date: Long, latitude: Double, longitude: Double) {
        val newCoordinate = Coordinate(latitude = latitude, longitude = longitude)
        val newImage = Image(uri = uri, date = date, coordinate = newCoordinate)
        _images = _images + (uri to newImage)
    }

    fun addAddressToImage(
        uri: String,
        locality: String?,
        subAdminArea: String?,
        adminArea: String?,
        countryName: String?
    ) {
        val address = Address(
            locality = locality,
            subAdminArea = subAdminArea,
            adminArea = adminArea,
            countryName = countryName
        )
        val image = _images[uri] ?: return
        val images = _addressImages.getOrDefault(address.name(), listOf()) + image
        _addressImages = _addressImages + (address.name() to images)
        _imageWithLocations.update { _addressImages.toStringImageViewDataList() }
        viewModelScope.launch(Dispatchers.IO) {
            locationRepository.fetchLocations(address = address)
        }
    }

    fun selectImages(address: String) {
        val images = _addressImages[address] ?: return
        _selectedImages.update { images.toImageViewDataList() }
    }

    fun visibleAreaChanged(boundingBoxViewData: BoundingBoxViewData) {
        val boundingBox = boundingBoxViewData.toBoundingBox()
        val visibleImages = _images.values.filter { image ->
            boundingBox.contains(coordinate = image.coordinate)
        }
        _visibleImages.update { visibleImages.toImageViewDataList() }
        _visibleRegions.update {
            _regionLocation.keys.filter {
                region -> boundingBox.contains(other = region.boundingBox)
            }.toRegionViewDataList()
        }
    }

    fun selectRegion(regionViewData: RegionViewData) {
        val region = regionViewData.toRegion()
        val location = _regionLocation[region] ?: return
        _currentLocation.update { location.toLocationViewData() }
    }

    fun switchRegion(regionViewData: RegionViewData) {
        val region = regionViewData.toRegion()
        val mutableRegionLocation = _regionLocation.toMutableMap()
        val location = mutableRegionLocation.remove(key = region) ?: return
        val locationUpdated = location.switch(region = region)

        _regionLocation.filter { it.value == location }.keys.forEach {
            mutableRegionLocation[if (it != region) it else region.switch()] = locationUpdated
        }
        _regionLocation = mutableRegionLocation.toMap()
        _currentLocation.update { locationUpdated.toLocationViewData() }

        if (location.administrativeUnit == AdministrativeUnit.CITY) {
            _locationAddress = _locationAddress + (location.address.name() to locationUpdated)
            _allLocationAddress.update { _locationAddress.toStringLocationViewData() }
        }
    }

    private fun onRegionLocationReady(regionLocation: Map<Region, Location>) {
        _regionLocation = _regionLocation + regionLocation
        regionLocation.values.forEach {
            if (it.administrativeUnit == AdministrativeUnit.CITY) {
                _locationAddress = _locationAddress + (it.address.name() to it)
            }
        }
        _allLocationAddress.update { _locationAddress.toStringLocationViewData() }
    }
}