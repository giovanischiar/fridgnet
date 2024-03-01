package io.schiar.fridgnet.model.repository

import io.schiar.fridgnet.model.Location
import io.schiar.fridgnet.model.Region
import io.schiar.fridgnet.model.repository.location.LocationRepository

class PolygonsRepository(private val locationRepository: LocationRepository) {
    fun currentLocation(): Location? {
        return locationRepository.currentLocation
    }

    suspend fun switchRegion(region: Region, onCurrentLocationChanged: () -> Unit) {
        locationRepository.switchRegion(region = region)
        onCurrentLocationChanged()
    }

    suspend fun switchAll(onCurrentLocationChanged: () -> Unit) {
        locationRepository.switchAll()
        onCurrentLocationChanged()
    }
}