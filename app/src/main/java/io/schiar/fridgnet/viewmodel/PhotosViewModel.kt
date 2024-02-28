package io.schiar.fridgnet.viewmodel

import androidx.lifecycle.ViewModel
import io.schiar.fridgnet.model.repository.PhotosRepository
import io.schiar.fridgnet.view.viewdata.BoundingBoxViewData
import io.schiar.fridgnet.view.viewdata.ImageViewData
import io.schiar.fridgnet.view.viewdata.LocationViewData
import io.schiar.fridgnet.viewmodel.util.toAddressImagesViewData
import io.schiar.fridgnet.viewmodel.util.toBoundingBoxViewData
import io.schiar.fridgnet.viewmodel.util.toLocationViewData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class PhotosViewModel(private val photosRepository: PhotosRepository) : ViewModel() {
    private val _selectedImages = MutableStateFlow<Pair<String, List<ImageViewData>>?>(
        value = null
    )
    val selectedImages: StateFlow<Pair<String, List<ImageViewData>>?> =
        _selectedImages.asStateFlow()

    private val _selectedLocation = MutableStateFlow<LocationViewData?>(value = null)
    val selectedLocation: StateFlow<LocationViewData?> = _selectedLocation

    private val _selectedBoundingBox = MutableStateFlow<BoundingBoxViewData?>(value = null)
    val selectedBoundingBox: StateFlow<BoundingBoxViewData?> = _selectedBoundingBox

    private val _selectedImagesBoundingBox = MutableStateFlow<BoundingBoxViewData?>(value = null)
    val selectedImagesBoundingBox: StateFlow<BoundingBoxViewData?> = _selectedImagesBoundingBox

    fun subscribe() {
        photosRepository.subscribeForNewImages(callback = ::updateCurrentImages)
    }

    fun updateCurrentImages() {
        _selectedImages.update { photosRepository.currentImages()?.toAddressImagesViewData() }
        _selectedLocation.update { photosRepository.selectedLocation()?.toLocationViewData() }
        _selectedBoundingBox.update { photosRepository.selectedBoundingBox()?.toBoundingBoxViewData() }
        _selectedImagesBoundingBox.update {
            photosRepository.selectedImagesBoundingBox()?.toBoundingBoxViewData()
        }
    }
}