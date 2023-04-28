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
    private var _images: List<Image> = emptyList()
    private val _visibleImages = MutableStateFlow(value = _images.toViewData())
    val visibleImages: StateFlow<List<ImageViewData>> = _visibleImages.asStateFlow()
    private val _allImages = MutableStateFlow(value = _images.toViewData())
    val allImages: StateFlow<List<ImageViewData>> = _allImages.asStateFlow()

    fun addImage(uri: String, date: Long, latitude: Double, longitude: Double) {
        val newLocation = Location(latitude = latitude, longitude = longitude)
        val newImage = Image(uri = uri, date = date, location = newLocation)
        _images = _images + newImage
        _allImages.update { _images.toViewData() }
    }

    fun visibleAreaChanged(bounds: LatLngBounds?) {
        val visibleImages = _images.filter { image ->
            val position = LatLng(image.location.latitude, image.location.longitude)
            bounds?.contains(position) == true
        }
        _visibleImages.update { visibleImages.toViewData() }
    }
}