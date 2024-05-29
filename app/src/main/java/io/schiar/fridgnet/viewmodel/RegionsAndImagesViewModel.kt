package io.schiar.fridgnet.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.schiar.fridgnet.model.repository.RegionsAndImagesRepository
import io.schiar.fridgnet.view.regionsandimages.uistate.BoundingBoxImagesUiState
import io.schiar.fridgnet.view.regionsandimages.uistate.VisibleImagesUiState
import io.schiar.fridgnet.view.regionsandimages.uistate.VisibleRegionsUiState
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
     * The stream (Flow) of ui state of visible Images converted into UI objects.
     */
    val visibleImagesUiStateFlow by lazy {
        mapRepository.imagesWithinCurrentBoundingBoxFlow
            .map { VisibleImagesUiState.VisibleImagesLoaded(it.toImageViewDataList()) }
    }

    /**
     * The stream (Flow) of ui state of visible Regions converted into UI objects.
     */
    val visibleRegionsUiStateFlow by lazy {
        mapRepository.activeRegionsWithinCurrentBoundingBoxFlow
            .map { VisibleRegionsUiState.VisibleRegionsLoaded(it.toRegionViewDataList()) }
    }

    /**
     * The stream (Flow) of ui state of bounding box of the visible Images converted into UI
     * objects.
     */
    val boundingBoxImagesUiStateFlow by lazy {
        mapRepository.boundingBoxImagesFlow
            .map {
                if (it == null) {
                    BoundingBoxImagesUiState.Loading
                } else {
                    BoundingBoxImagesUiState.BoundingBoxImagesLoaded(it.toBoundingBoxViewData())
                }
            }
    }

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