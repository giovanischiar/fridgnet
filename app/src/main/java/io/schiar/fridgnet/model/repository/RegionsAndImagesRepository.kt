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

class RegionsAndImagesRepository @Inject constructor(
    private val administrativeUnitDataSource: AdministrativeUnitDataSource,
    imageDataSource: ImageDataSource,
    private val currentRegionDataSource: CurrentRegionDataSource
) {
    private val currentBoundingBoxFlow = MutableStateFlow<BoundingBox?>(value = null)
    private var activeRegionsWithinCurrentBoundingBox = emptyList<Region>()

    @OptIn(ExperimentalCoroutinesApi::class)
    val activeRegionsWithinCurrentBoundingBoxFlow = currentBoundingBoxFlow.filterNotNull()
        .flatMapLatest { boundingBox ->
            administrativeUnitDataSource.retrieveActiveRegionsWithin(boundingBox)
        }.onEach { activeRegionsWithinCurrentBoundingBox = it }
    val imagesWithinCurrentBoundingBoxFlow = imageDataSource.retrieve().combine(
        flow = currentBoundingBoxFlow.filterNotNull(),
        transform = ::combineImagesAndCurrentBoundingBoxFlows
    )
    private val boundingBoxPhotosStateFlow = MutableStateFlow<BoundingBox?>(value = null)
    val boundingBoxImagesFlow: Flow<BoundingBox?> = boundingBoxPhotosStateFlow

    private fun combineImagesAndCurrentBoundingBoxFlows(
        images: List<Image>, currentBoundingBox: BoundingBox
    ): List<Image> {
        boundingBoxPhotosStateFlow.update { images.mergeToBoundingBox() }
        return images.filter { currentBoundingBox.contains(geoLocation = it.geoLocation) }
    }

    fun updateBoundingBox(boundingBox: BoundingBox) {
        Log.d("", "updateBoundingBox")
        currentBoundingBoxFlow.update { boundingBox }
    }

    fun selectActiveRegionAt(index: Int) {
        Log.d("", "selectActiveRegionAt($index)")
        currentRegionDataSource.update(region = activeRegionsWithinCurrentBoundingBox[index])
    }
}