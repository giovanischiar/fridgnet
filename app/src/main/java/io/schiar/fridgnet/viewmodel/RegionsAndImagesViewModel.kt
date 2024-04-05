package io.schiar.fridgnet.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.schiar.fridgnet.model.repository.RegionsAndImagesRepository
import io.schiar.fridgnet.view.shared.viewdata.BoundingBoxViewData
import io.schiar.fridgnet.viewmodel.util.toBoundingBox
import io.schiar.fridgnet.viewmodel.util.toBoundingBoxViewData
import io.schiar.fridgnet.viewmodel.util.toImageViewDataList
import io.schiar.fridgnet.viewmodel.util.toRegionViewDataList
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class RegionsAndImagesViewModel @Inject constructor(
    private val mapRepository: RegionsAndImagesRepository
) : ViewModel() {
    val visibleImagesFlow = mapRepository.imagesWithinCurrentBoundingBoxFlow
        .map { it.toImageViewDataList() }
    val visibleRegionsFlow = mapRepository.activeRegionsWithinCurrentBoundingBoxFlow
        .map { it.toRegionViewDataList() }
    val boundingBoxImagesFlow = mapRepository.boundingBoxImagesFlow
        .map { it?.toBoundingBoxViewData() }

    fun selectRegionAt(index: Int) { mapRepository.selectActiveRegionAt(index = index) }

    fun visibleAreaChanged(boundingBoxViewData: BoundingBoxViewData) {
        mapRepository.updateBoundingBox(boundingBox = boundingBoxViewData.toBoundingBox())
    }
}