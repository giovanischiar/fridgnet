package io.schiar.fridgnet.viewmodel

import androidx.lifecycle.ViewModel
import io.schiar.fridgnet.model.repository.Repository
import io.schiar.fridgnet.view.viewdata.ImageViewData
import io.schiar.fridgnet.viewmodel.util.toAddressImagesViewData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class PhotosViewModel(private val repository: Repository): ViewModel() {
    private val _selectedImages = MutableStateFlow<Pair<String, List<ImageViewData>>?>(
        value = null
    )
    val selectedImages: StateFlow<Pair<String, List<ImageViewData>>?>
        = _selectedImages.asStateFlow()

    fun subscribe() {
        repository.subscribeForNewImages(callback = ::updateCurrentImages)
    }

    fun updateCurrentImages() {
        _selectedImages.update { repository.currentImages()?.toAddressImagesViewData() }
    }
}