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

/**
 * The RegionsAndImagesViewModel is the point of connection between the Regions and Images screen
 * and its Repository
 */
@HiltViewModel
class RegionsAndImagesViewModel @Inject constructor(
    private val mapRepository: RegionsAndImagesRepository
) : ViewModel() {
    /**
     * The stream (Flow) of visible Images converted into UI objects.
     */
    val visibleImagesFlow = mapRepository.imagesWithinCurrentBoundingBoxFlow
        .map { it.toImageViewDataList() }

    /**
     * The stream (Flow) of visible Regions converted into UI objects.
     */
    val visibleRegionsFlow = mapRepository.activeRegionsWithinCurrentBoundingBoxFlow
        .map { it.toRegionViewDataList() }

    /**
     * The stream (Flow) of bounding box of the visible Images converted into UI objects.
     */
    val boundingBoxImagesFlow = mapRepository.boundingBoxImagesFlow
        .map { it?.toBoundingBoxViewData() }

    /**
     * Delegates the repository to update the current region in the model. It creates a coroutine to
     * do that.
     */
    fun selectRegionAt(index: Int) { mapRepository.selectActiveRegionAt(index = index) }

    /**
     * Delegates the repository to update the current region (bounding box) in the model. It creates
     * a coroutine to do that.
     *
     * @param boundingBoxViewData the object that represent the visible map area for the user.
     */
    fun visibleAreaChanged(boundingBoxViewData: BoundingBoxViewData) {
        mapRepository.updateBoundingBox(boundingBox = boundingBoxViewData.toBoundingBox())
    }
}