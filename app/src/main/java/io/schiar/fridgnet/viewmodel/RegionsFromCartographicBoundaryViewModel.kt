package io.schiar.fridgnet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.schiar.fridgnet.model.repository.RegionsFromCartographicBoundaryRepository
import io.schiar.fridgnet.viewmodel.util.toCartographicBoundaryViewData
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * The RegionsAndImagesViewModel is the point of connection between the Regions From Cartographic
 * boundary screen and its Repository
 */
@HiltViewModel
class RegionsFromCartographicBoundaryViewModel @Inject constructor(
    private val regionsFromCartographicBoundaryRepository: RegionsFromCartographicBoundaryRepository
) : ViewModel() {
    /**
     * The stream (Flow) of the current cartographic boundary converted into UI object.
     */
    val currentCartographicBoundaryFlow = regionsFromCartographicBoundaryRepository
        .currentCartographicBoundaryFlow
        .map { it.toCartographicBoundaryViewData() }

    /**
     * Delegates the repository to update the current cartographic boundary's region in the model.
     * It creates a coroutine to do that.
     *
     * @param index the index of the region to perform the switch
     */
    fun switchRegionAt(index: Int) = viewModelScope.launch {
        regionsFromCartographicBoundaryRepository.switchRegionAt(index = index)
    }

    /**
     * Delegates the repository to update the all of the regions of the current cartographic
     * boundary in the model.It creates a coroutine to do that.
     */
    fun switchAll() = viewModelScope.launch {
        regionsFromCartographicBoundaryRepository.switchAll()
    }
}