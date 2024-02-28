package io.schiar.fridgnet.model.repository

import io.schiar.fridgnet.model.Location
import io.schiar.fridgnet.model.Region
import io.schiar.fridgnet.model.repository.location.LocationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PolygonsRepository(private val locationRepository: LocationRepository) {
    fun currentLocation(): Location? {
        return locationRepository.currentLocation
    }

    suspend fun switchRegion(region: Region, onCurrentLocationChanged: () -> Unit) {
        withContext(Dispatchers.IO) { locationRepository.switchRegion(region = region) }
        onCurrentLocationChanged()
    }

    suspend fun switchAll(onCurrentLocationChanged: () -> Unit) {
        withContext(Dispatchers.IO) { locationRepository.switchAll() }
        onCurrentLocationChanged()
    }
}