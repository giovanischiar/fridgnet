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
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(
    private val locationRepository: LocationRepository
): ViewModel() {
    private var _images: Map<String, Image> = emptyMap()
    private var _addressImages: Map<String, List<Image>> = emptyMap()
    private var _locationAddress: Map<String, Location> = emptyMap()
    private var _regionLocation: Map<Region, Location> = emptyMap()

    private val _visibleImages = MutableStateFlow(value = _images.toImageViewData())
    val visibleImages: StateFlow<List<ImageViewData>> = _visibleImages.asStateFlow()

    private val _selectedImages = MutableStateFlow<Pair<String, List<ImageViewData>>>(
        value = Pair("", emptyList())
    )
    val selectedImages: StateFlow<Pair<String, List<ImageViewData>>> = _selectedImages.asStateFlow()

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

    private var _allPhotosBoundingBox = MutableStateFlow<BoundingBoxViewData?>(value = null)
    val allPhotosBoundingBox: StateFlow<BoundingBoxViewData?> = _allPhotosBoundingBox

    private var _databaseLoaded = MutableStateFlow(value = false)
    val databaseLoaded: StateFlow<Boolean> = _databaseLoaded

    suspend fun loadDatabase() = coroutineScope {
        launch {
            withContext(Dispatchers.IO) { locationRepository.setup() }
            _databaseLoaded.update { true }
        }
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
            locationRepository.fetch(
                address = address,
                onLocationReady = this@MainViewModel::onLocationReady
            )
        }
    }

    fun selectImages(address: String) {
        val images = _addressImages[address] ?: return
        _selectedImages.update { address to images.toImageViewDataList() }
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
        updateBoundingBox()
        _currentLocation.update { locationUpdated.toLocationViewData() }

        if (location.address.administrativeUnit == AdministrativeUnit.CITY) {
            _locationAddress = _locationAddress + (location.address.name() to locationUpdated)
            _allLocationAddress.update { _locationAddress.toStringLocationViewData() }
        }
    }

    private fun onLocationReady(location: Location) {
        for (region in location.regions) {
            onRegionLocationReady(mapOf(region to location))
        }
    }

    private fun onRegionLocationReady(regionLocation: Map<Region, Location>) {
        _regionLocation = _regionLocation + regionLocation

        regionLocation.values.forEach { location ->
            if (location.address.administrativeUnit == AdministrativeUnit.CITY) {
                _locationAddress = _locationAddress + (location.address.name() to location)

                _allPhotosBoundingBox.update {
                    val allPhotosBoundingBox = _allPhotosBoundingBox.value
                    if (allPhotosBoundingBox == null) {
                        location.boundingBox.toBoundingBoxViewData()
                    } else {
                        (allPhotosBoundingBox.toBoundingBox() + location.boundingBox)
                            .toBoundingBoxViewData()
                    }
                }
            }
        }

        _allLocationAddress.update { _locationAddress.toStringLocationViewData() }
    }

    private fun updateBoundingBox() {
        _allPhotosBoundingBox.update { (_regionLocation.values.filter {
            it.address.administrativeUnit == AdministrativeUnit.CITY
        }.map { it.boundingBox }.reduce{ acc, boundingBox -> acc + boundingBox }).toBoundingBoxViewData()}
    }
}