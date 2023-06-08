package io.schiar.fridgnet.viewmodel

import androidx.lifecycle.ViewModel
import io.schiar.fridgnet.model.AdministrativeUnit
import io.schiar.fridgnet.model.Image
import io.schiar.fridgnet.model.Location
import io.schiar.fridgnet.model.repository.Repository
import io.schiar.fridgnet.view.viewdata.BoundingBoxViewData
import io.schiar.fridgnet.view.viewdata.ImageViewData
import io.schiar.fridgnet.view.viewdata.LocationViewData
import io.schiar.fridgnet.view.viewdata.RegionViewData
import io.schiar.fridgnet.viewmodel.util.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MainViewModel(private val repository: Repository): ViewModel() {
    // MapScreen
    private val _visibleImages = MutableStateFlow<List<ImageViewData>>(value = emptyList())
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
    private val _cityNameImages = MutableStateFlow<Map<String, List<ImageViewData>>>(
        value = emptyMap()
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
    suspend fun loadDatabase() {
        repository.loadDatabase(onDatabaseLoaded = ::onDatabaseLoaded)
    }

    private fun onDatabaseLoaded() {
        _databaseLoaded.update { true }
    }

    suspend fun addURIs(uris: List<String>) {
        repository.subscribeForAddressImageAdded(callback = ::onAddressAddedOnImage)
        repository.subscribeForLocationsReady(callback = ::onLocationReady)
        repository.addURIs(uris = uris)
    }

    private fun onAddressAddedOnImage(address: String, images: List<Image>) {
        _cityNameImages.update { it + (address to images.toImageViewDataList()) }
    }

    // HomeScreen
    fun selectImages(address: String) {
        val images = repository.selectImagesFrom(addressName = address) ?: return
        _selectedImages.update { address to images.toImageViewDataList() }
    }

    // MapScreen
    fun visibleAreaChanged(boundingBoxViewData: BoundingBoxViewData) {
        val boundingBox = boundingBoxViewData.toBoundingBox()
        _visibleImages.update {
            repository.visibleImages(boundingBox = boundingBox).toImageViewDataList()
        }
        _visibleRegions.update {
            repository.visibleRegions(boundingBox = boundingBox).toRegionViewDataList()
        }
    }

    fun zoomToFitAllCities() {
        _allPhotosBoundingBox.update { repository.boundingBoxCities()?.toBoundingBoxViewData() }
    }

    fun selectRegion(regionViewData: RegionViewData) {
        val region = regionViewData.toRegion()
        updateCurrentLocation(location = repository.selectNewLocationFrom(region = region))
    }

    // PolygonsScreen
    suspend fun switchRegion(regionViewData: RegionViewData) {
        repository.switchRegion(
            region = regionViewData.toRegion(),
            onCurrentLocationChanged = ::updateCurrentLocation
        )
    }

    suspend fun switchAll() {
        repository.switchAll(onCurrentLocationChanged = ::updateCurrentLocation)
    }

    private fun updateCurrentLocation(location: Location?) {
        _currentLocation.update { location?.toLocationViewData() }
    }

    private fun onLocationReady(location: Location) {
        if (location.address.administrativeUnit == AdministrativeUnit.CITY) {
            _cityNameLocation.update {
                it + (location.address.name() to location.toLocationViewData())
            }
        }
    }
}