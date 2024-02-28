package io.schiar.fridgnet.model.repository

import io.schiar.fridgnet.model.BoundingBox
import io.schiar.fridgnet.model.Image
import io.schiar.fridgnet.model.Region
import io.schiar.fridgnet.model.repository.image.ImageRepository
import io.schiar.fridgnet.model.repository.location.LocationRepository

class MapRepository(
    private val locationRepository: LocationRepository,
    private val imageRepository: ImageRepository
) {
    fun selectNewLocationFrom(region: Region) {
        locationRepository.selectNewLocationFrom(region = region)
    }

    fun visibleImages(boundingBox: BoundingBox): List<Image> {
        return imageRepository.imagesThatIntersect(boundingBox = boundingBox)
    }

    fun visibleRegions(boundingBox: BoundingBox): List<Region> {
        return locationRepository.regionsThatIntersect(boundingBox = boundingBox)
    }

    fun boundingBoxCities(): BoundingBox? {
        return locationRepository.allCitiesBoundingBox
    }
}