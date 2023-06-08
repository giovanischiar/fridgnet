package io.schiar.fridgnet.viewmodel

import androidx.lifecycle.ViewModel
import io.schiar.fridgnet.model.*
import io.schiar.fridgnet.model.repository.address.AddressRepository
import io.schiar.fridgnet.model.repository.location.LocationRepository
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
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.util.*

class MainViewModel(
    private val locationRepository: LocationRepository,
    private val addressRepository: AddressRepository
): ViewModel() {
    // MapScreen
    private val _images: MutableMap<String, Image> = Collections.synchronizedMap(mutableMapOf())

    private val _visibleImages = MutableStateFlow(value = _images.toImageViewData())
    val visibleImages: StateFlow<List<ImageViewData>> = _visibleImages.asStateFlow()

    private val _visibleRegions: MutableStateFlow<List<RegionViewData>>
            = MutableStateFlow(emptyList())
    val visibleRegions: StateFlow<List<RegionViewData>> = _visibleRegions.asStateFlow()

    // PhotosScreen
    private val _selectedImages = MutableStateFlow<Pair<String, List<ImageViewData>>>(
        value = Pair("", emptyList())
    )
    val selectedImages: StateFlow<Pair<String, List<ImageViewData>>> = _selectedImages.asStateFlow()

    // HomeScreen
    private var _addressImages: Map<String, List<Image>> = emptyMap()

    private val _cityNameImages = MutableStateFlow(
        value = _addressImages.toStringImageViewDataList()
    )
    val cityNameImages: StateFlow<Map<String, List<ImageViewData>>> =
        _cityNameImages.asStateFlow()

    private val _cityNameLocation = MutableStateFlow<Map<String, LocationViewData>>(
        value = emptyMap()
    )
    val cityNameLocation: StateFlow<Map<String, LocationViewData>> = _cityNameLocation.asStateFlow()

    // PolygonsScreen
    private val _currentLocation = MutableStateFlow<LocationViewData?>(null)
    val currentLocation: StateFlow<LocationViewData?> = _currentLocation.asStateFlow()

    private var _allPhotosBoundingBox = MutableStateFlow<BoundingBoxViewData?>(value = null)
    val allPhotosBoundingBox: StateFlow<BoundingBoxViewData?> = _allPhotosBoundingBox

    // FridgeApp
    private var _databaseLoaded = MutableStateFlow(value = false)
    val databaseLoaded: StateFlow<Boolean> = _databaseLoaded

    // FridgeApp
    suspend fun loadDatabase() = coroutineScope {
        withContext(Dispatchers.IO) { locationRepository.setup() }
        _databaseLoaded.update { true }
    }

    suspend fun addImage(uri: String, date: Long, latitude: Double, longitude: Double) {
        val coordinate = Coordinate(latitude = latitude, longitude = longitude)
        _images[uri] = Image(uri = uri, date = date, coordinate = coordinate)
        addressRepository.getAddressFrom(
            coordinate = coordinate,
            onReady = { onAddressReady(uri = uri, address = it) }
        )
    }

    private fun onAddressReady(uri: String, address: Address) = runBlocking {
        addAddressToImage(uri = uri, address = address)
        locationRepository.loadRegions(address = address, ::onLocationReady)
    }

    private fun addAddressToImage(uri: String, address: Address) {
        val image = _images[uri] ?: return
        val images = _addressImages.getOrDefault(address.name(), listOf()) + image
        _addressImages = _addressImages + (address.name() to images)
        _cityNameImages.update { _addressImages.toStringImageViewDataList() }
    }

    // HomeScreen
    fun selectImages(address: String) {
        val images = _addressImages[address] ?: return
        _selectedImages.update { address to images.toImageViewDataList() }
    }

    // MapScreen
    fun visibleAreaChanged(boundingBoxViewData: BoundingBoxViewData) {
        val boundingBox = boundingBoxViewData.toBoundingBox()
        val visibleImages = _images.values.filter { image ->
            boundingBox.contains(coordinate = image.coordinate)
        }
        _visibleImages.update { visibleImages.toImageViewDataList() }
        _visibleRegions.update {
            val regions = locationRepository.regionsThatIntersect(boundingBox = boundingBox)
            regions.toRegionViewDataList()
        }
    }

    fun zoomToFitAllCities() {
        _allPhotosBoundingBox.update {
            locationRepository.allCitiesBoundingBox?.toBoundingBoxViewData()
        }
    }

    fun selectRegion(regionViewData: RegionViewData) {
        val region = regionViewData.toRegion()
        locationRepository.selectNewLocationFrom(region = region)
        updateCurrentLocation()
    }

    // PolygonsScreen
    suspend fun switchRegion(regionViewData: RegionViewData) {
        locationRepository.switchRegion(region = regionViewData.toRegion())
        updateCurrentLocation()
    }

    suspend fun switchAll() {
        locationRepository.switchAll()
        updateCurrentLocation()
    }

    private fun updateCurrentLocation() {
        _currentLocation.update { locationRepository.currentLocation?.toLocationViewData() }
    }

    private fun onLocationReady(location: Location) {
        if (location.address.administrativeUnit == AdministrativeUnit.CITY) {
            _cityNameLocation.update {
                it + (location.address.name() to location.toLocationViewData())
            }
        }
    }
}