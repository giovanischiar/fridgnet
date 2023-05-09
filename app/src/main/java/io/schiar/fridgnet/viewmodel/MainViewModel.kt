package io.schiar.fridgnet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import io.schiar.fridgnet.model.*
import io.schiar.fridgnet.model.nominatim.PolygonSearcher
import io.schiar.fridgnet.view.viewdata.ImageViewData
import io.schiar.fridgnet.view.viewdata.LocationViewData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel: ViewModel() {
    private var _images: Map<String, Image> = emptyMap()
    private var _addressImages: Map<Address, List<Image>> = emptyMap()
    private var _locationAddress: Map<Address, Location> = emptyMap()

    private val _visibleImages = MutableStateFlow(value = _images.toListImagesViewData())
    val visibleImages: StateFlow<List<ImageViewData>> = _visibleImages.asStateFlow()

    private val _selectedImages = MutableStateFlow<List<ImageViewData>>(value = emptyList())
    val selectedImages: StateFlow<List<ImageViewData>> = _selectedImages.asStateFlow()

    private val _imageWithLocations = MutableStateFlow(
        value = _addressImages.toAddressImageListViewData()
    )
    val imagesWithLocation: StateFlow<Map<Address, List<ImageViewData>>> =
        _imageWithLocations.asStateFlow()

    private val _allLocationAddress = MutableStateFlow(
        value = _locationAddress.toAddressLocationViewData()
    )
    val allLocationAddress: StateFlow<Map<Address, LocationViewData>> =
        _allLocationAddress.asStateFlow()

    fun addImage(uri: String, date: Long, latitude: Double, longitude: Double) {
        val newCoordinate = Coordinate(latitude = latitude, longitude = longitude)
        val newImage = Image(uri = uri, date = date, coordinate = newCoordinate)
        _images = _images + (uri to newImage)
    }

    fun addAddressToImage(uri: String, systemAddress: android.location.Address) {
        val address = systemAddress.toAddress()
        if (_addressImages.containsKey(address) && _images.containsKey(uri)) {
            val newList = _addressImages[address]!! + _images[uri]!!
            _addressImages = _addressImages + (address to newList)
        } else if (_images.containsKey(uri)) {
            _addressImages = _addressImages + (address to listOf(_images[uri]!!))
        }
        _imageWithLocations.update { _addressImages.toAddressImageListViewData() }
        searchForLocation(address)
    }

    private fun searchForLocation(address: Address) {
        val polygonSearcher = PolygonSearcher(address = address)
        viewModelScope.launch(Dispatchers.IO) {
            val results = withContext(Dispatchers.Default) { polygonSearcher.search() }
            val bodyList = results.body() ?: emptyList()
            if (bodyList.isNotEmpty()) {
                val body = bodyList[0]
                val geoJson = body.geojson
                val boundingBox = body.boundingbox.toBoundingBox()
                val location = when (geoJson.type) {
                    "Point" -> {
                        val pointDouble = geoJson.coordinates as List<Double>
                        LineStringLocation(
                            region = listOf(pointDouble.toCoordinate()),
                            boundingBox = boundingBox
                        )
                    }
                    "LineString" -> {
                        val polygonDouble = geoJson.coordinates as List<List<Double>>
                        LineStringLocation(
                            region = polygonDouble.toLineStringCoordinates(),
                            boundingBox = boundingBox
                        )
                    }

                    "Polygon" -> {
                        val polygonDouble = geoJson.coordinates as List<List<List<Double>>>
                        PolygonLocation(
                            region = polygonDouble.toPolygonCoordinates(),
                            boundingBox = boundingBox
                        )
                    }

                    "MultiPolygon" -> {
                        val multipolygonDouble = geoJson.coordinates as List<List<List<List<Double>>>>
                        MultiPolygonLocation(
                            region = multipolygonDouble.toMultiPolygonCoordinates(),
                            boundingBox = boundingBox
                        )
                    }

                    else -> return@launch
                }
                _locationAddress = _locationAddress + (address to location)
                _allLocationAddress.update { _locationAddress.toAddressLocationViewData() }
            }
        }
    }

    fun selectImages(address: Address) {
        if (_addressImages.containsKey(address)) {
            _selectedImages.update { _addressImages[address]!!.toViewData() }
        }
    }

    fun visibleAreaChanged(bounds: LatLngBounds?) {
        val visibleImages = _images.values.filter { image ->
            val position = LatLng(image.coordinate.latitude, image.coordinate.longitude)
            bounds?.contains(position) == true
        }
        _visibleImages.update { visibleImages.toViewData() }
    }
}