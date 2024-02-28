package io.schiar.fridgnet.viewmodel

import androidx.lifecycle.ViewModel
import io.schiar.fridgnet.model.repository.MapRepository
import io.schiar.fridgnet.view.viewdata.BoundingBoxViewData
import io.schiar.fridgnet.view.viewdata.ImageViewData
import io.schiar.fridgnet.view.viewdata.RegionViewData
import io.schiar.fridgnet.viewmodel.util.toBoundingBox
import io.schiar.fridgnet.viewmodel.util.toBoundingBoxViewData
import io.schiar.fridgnet.viewmodel.util.toImageViewDataList
import io.schiar.fridgnet.viewmodel.util.toRegion
import io.schiar.fridgnet.viewmodel.util.toRegionViewDataList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MapViewModel(private val mapRepository: MapRepository) : ViewModel() {
    private val _visibleImages = MutableStateFlow<List<ImageViewData>>(value = emptyList())
    val visibleImages: StateFlow<List<ImageViewData>> = _visibleImages.asStateFlow()

    private val _visibleRegions: MutableStateFlow<List<RegionViewData>> =
        MutableStateFlow(emptyList())
    val visibleRegions: StateFlow<List<RegionViewData>> = _visibleRegions.asStateFlow()

    private var _allPhotosBoundingBox = MutableStateFlow<BoundingBoxViewData?>(value = null)
    val allPhotosBoundingBox: StateFlow<BoundingBoxViewData?> = _allPhotosBoundingBox

    fun selectRegion(regionViewData: RegionViewData) {
        val region = regionViewData.toRegion()
        mapRepository.selectNewLocationFrom(region = region)
    }

    fun visibleAreaChanged(boundingBoxViewData: BoundingBoxViewData) {
        val boundingBox = boundingBoxViewData.toBoundingBox()
        _visibleImages.update {
            mapRepository.visibleImages(boundingBox = boundingBox).toImageViewDataList()
        }

        _visibleRegions.update {
            mapRepository.visibleRegions(boundingBox = boundingBox).toRegionViewDataList()
        }
    }

    fun zoomToFitAllCities() {
        _allPhotosBoundingBox.update { mapRepository.boundingBoxCities()?.toBoundingBoxViewData() }
    }
}