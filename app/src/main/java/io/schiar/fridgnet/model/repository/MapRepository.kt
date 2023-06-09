package io.schiar.fridgnet.model.repository

import io.schiar.fridgnet.model.BoundingBox
import io.schiar.fridgnet.model.Image
import io.schiar.fridgnet.model.Region

interface MapRepository {
    fun selectNewLocationFrom(region: Region)
    fun visibleImages(boundingBox: BoundingBox): List<Image>
    fun visibleRegions(boundingBox: BoundingBox): List<Region>
    fun boundingBoxCities(): BoundingBox?
}