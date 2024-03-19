package io.schiar.fridgnet.model.repository

import io.schiar.fridgnet.Log
import io.schiar.fridgnet.model.BoundingBox
import io.schiar.fridgnet.model.Image
import io.schiar.fridgnet.model.Region
import io.schiar.fridgnet.model.datasource.CurrentRegionDataSource
import io.schiar.fridgnet.model.datasource.ImageDataSource
import io.schiar.fridgnet.model.datasource.CartographicBoundaryDataSource
import io.schiar.fridgnet.model.mergeToBoundingBox
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import kotlin.time.measureTime

class MapRepository(
    cartographicBoundaryDataSource: CartographicBoundaryDataSource,
    imageDataSource: ImageDataSource,
    private val currentRegionDataSource: CurrentRegionDataSource
) {
    private val currentBoundingBoxFlow = MutableStateFlow<BoundingBox?>(value = null)
    private val _regionsMutableSet = mutableSetOf<Region>()
    private var _visibleRegions = emptyList<Region>()

    val visibleRegions = cartographicBoundaryDataSource.retrieveRegions().flowOn(Dispatchers.IO).combine(
        flow = currentBoundingBoxFlow,
        transform = ::combineRegionsCurrentBoundingBox
    )
    val visibleImages = imageDataSource.retrieve().combine(
        flow = currentBoundingBoxFlow,
        transform = ::combineImagesCurrentBoundingBox
    )
    private val boundingBoxPhotosFlow = MutableStateFlow<BoundingBox?>(value = null)
    val boundingBoxImages: Flow<BoundingBox?> = boundingBoxPhotosFlow

    private fun combineImagesCurrentBoundingBox(
        images: List<Image>, currentBoundingBox: BoundingBox?
    ): List<Image> {
        return if (currentBoundingBox == null) {
            emptyList()
        } else {
            boundingBoxPhotosFlow.update { images.mergeToBoundingBox() }
            images.filter { currentBoundingBox.contains(geoLocation = it.geoLocation) }
        }
    }

    private suspend fun combineRegionsCurrentBoundingBox(
        regions: List<Region>, currentBoundingBox: BoundingBox?
    ): List<Region> {
        Log.d("", "Combining regions")
        return if (currentBoundingBox == null) {
            emptyList()
        } else {
            withContext(Dispatchers.Default) {
                Log.d("", "there are ${regions.size} regions")
                val time = measureTime {
                    for (region in regions) {
                        if (region.active && currentBoundingBox.contains(region.boundingBox)) {
                            _regionsMutableSet.add(element = region)
                        } else {
                            _regionsMutableSet.remove(element = region)
                        }
                    }

                    _visibleRegions = _regionsMutableSet.toList()
                }
                Log.d("", "there are ${_visibleRegions.size} visible regions")
                Log.d("", "combineLocationsCurrentBoundingBox took $time")
            }
            _visibleRegions
        }
    }

    fun updateBoundingBox(boundingBox: BoundingBox) {
        Log.d("", "updateBoundingBox")
        currentBoundingBoxFlow.update { boundingBox }
    }

    fun selectVisibleRegionAt(index: Int) {
        Log.d("", "selectNewLocationFrom($index)")
        currentRegionDataSource.update(region = _visibleRegions[index])
    }
}