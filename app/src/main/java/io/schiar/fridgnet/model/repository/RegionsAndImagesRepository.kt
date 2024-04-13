package io.schiar.fridgnet.model.repository

import io.schiar.fridgnet.Log
import io.schiar.fridgnet.model.BoundingBox
import io.schiar.fridgnet.model.Image
import io.schiar.fridgnet.model.Region
import io.schiar.fridgnet.model.datasource.AdministrativeUnitDataSource
import io.schiar.fridgnet.model.datasource.CurrentRegionDataSource
import io.schiar.fridgnet.model.datasource.ImageDataSource
import io.schiar.fridgnet.model.mergeToBoundingBox
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

/**
 * The repository that exposes and manipulates Regions and Images screen's flows.
 */
class RegionsAndImagesRepository @Inject constructor(
    private val administrativeUnitDataSource: AdministrativeUnitDataSource,
    imageDataSource: ImageDataSource,
    private val currentRegionDataSource: CurrentRegionDataSource
) {
    private val currentBoundingBoxFlow = MutableStateFlow<BoundingBox?>(value = null)
    private var activeRegionsWithinCurrentBoundingBox = emptyList<Region>()

    /**
     * Retrieves from data source all active regions within the current bounding box. When the user
     * move the map with gestures the visible area will change this change will send a new bounding
     * box, triggering this flow to send the regions within the new bounding box.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    val activeRegionsWithinCurrentBoundingBoxFlow = currentBoundingBoxFlow.filterNotNull()
        .flatMapLatest { boundingBox ->
            administrativeUnitDataSource.retrieveActiveRegionsWithin(boundingBox)
        }.onEach { activeRegionsWithinCurrentBoundingBox = it }

    /**
     * Retrieves from data source all images within the current bounding box. When the user
     * move the map with gestures the visible area will change this change will send a new bounding
     * box, triggering this flow to send the images within the new bounding box.
     */
    val imagesWithinCurrentBoundingBoxFlow = imageDataSource.retrieve().combine(
        flow = currentBoundingBoxFlow.filterNotNull(),
        transform = ::combineImagesAndCurrentBoundingBoxFlows
    )
    private val boundingBoxPhotosStateFlow = MutableStateFlow<BoundingBox?>(value = null)

    /**
     * the bounding box that encloses all images, used in the screen to power the zoom to fit
     * feature.
     */
    val boundingBoxImagesFlow: Flow<BoundingBox?> = boundingBoxPhotosStateFlow

    private fun combineImagesAndCurrentBoundingBoxFlows(
        images: List<Image>, currentBoundingBox: BoundingBox
    ): List<Image> {
        boundingBoxPhotosStateFlow.update { images.mergeToBoundingBox() }
        return images.filter { currentBoundingBox.contains(geoLocation = it.geoLocation) }
    }

    /**
     * the method triggers when the visible map area changes, generating a new bounding box.
     *
     * @param boundingBox the bounding box generated
     */
    fun updateBoundingBox(boundingBox: BoundingBox) {
        Log.d("", "updateBoundingBox")
        currentBoundingBoxFlow.update { boundingBox }
    }

    /**
     * the method triggers when the user select a region in the map. The method update the region to
     * the datasource.
     *
     * @param index the index of the region
     */
    fun selectActiveRegionAt(index: Int) {
        Log.d("", "selectActiveRegionAt($index)")
        currentRegionDataSource.update(region = activeRegionsWithinCurrentBoundingBox[index])
    }
}