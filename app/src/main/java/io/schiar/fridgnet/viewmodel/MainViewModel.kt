package io.schiar.fridgnet.viewmodel

import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import io.schiar.fridgnet.model.Image
import io.schiar.fridgnet.model.Location
import io.schiar.fridgnet.view.viewdata.ImageViewData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MainViewModel: ViewModel() {
    private var _images: Map<String, Image> = emptyMap()
    private var _addressImages: Map<String, List<Image>> = emptyMap()

    private val _visibleImages = MutableStateFlow(value = _images.toListImagesViewData())
    val visibleImages: StateFlow<List<ImageViewData>> = _visibleImages.asStateFlow()

    private val _selectedImages = MutableStateFlow<List<ImageViewData>>(value = emptyList())
    val selectedImages: StateFlow<List<ImageViewData>> = _selectedImages.asStateFlow()

    private val _imageWithLocations = MutableStateFlow(
        value = mapOf<String, List<ImageViewData>>()
    )
    val imagesWithLocation: StateFlow<Map<String, List<ImageViewData>>> =
        _imageWithLocations.asStateFlow()

    fun addImage(uri: String, date: Long, latitude: Double, longitude: Double) {
        val newLocation = Location(latitude = latitude, longitude = longitude)
        val newImage = Image(uri = uri, date = date, location = newLocation)
        _images = _images + (uri to newImage)
    }

    fun addLocationToImage(uri: String, address: String) {
        if (_addressImages.containsKey(address) && _images.containsKey(uri)) {
            val newList = _addressImages[address]!! + _images[uri]!!
            _addressImages = _addressImages + (address to newList)
        } else if (_images.containsKey(uri)) {
            _addressImages = _addressImages + (address to listOf(_images[uri]!!))
        }
        _imageWithLocations.update { _addressImages.toViewData() }
    }

    fun selectImages(address: String) {
        if (_addressImages.containsKey(address)) {
            _selectedImages.update { _addressImages[address]!!.toViewData() }
        }
    }

    fun visibleAreaChanged(bounds: LatLngBounds?) {
        val visibleImages = _images.values.filter { image ->
            val position = LatLng(image.location.latitude, image.location.longitude)
            bounds?.contains(position) == true
        }
        _visibleImages.update { visibleImages.toViewData() }
    }
}