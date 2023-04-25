package io.schiar.fridgnet.viewmodel

import androidx.lifecycle.ViewModel
import io.schiar.fridgnet.model.Image
import io.schiar.fridgnet.model.Location
import io.schiar.fridgnet.view.viewdata.ImageViewData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MainViewModel: ViewModel() {
    private var _images: List<Image> = emptyList()
    private val _imagesViewData = MutableStateFlow(value = _images.toViewData())
    val images: StateFlow<List<ImageViewData>> = _imagesViewData.asStateFlow()

    fun addImage(uri: String, date: Long, latitude: Double, longitude: Double) {
        val newLocation = Location(latitude = latitude, longitude = longitude)
        val newImage = Image(uri = uri, date = date, location = newLocation)
        _images = listOf(*_images.toTypedArray(), newImage)
        _imagesViewData.update { _images.toViewData() }
    }
}