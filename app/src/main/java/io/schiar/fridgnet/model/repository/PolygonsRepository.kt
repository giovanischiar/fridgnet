package io.schiar.fridgnet.model.repository

import io.schiar.fridgnet.model.Location
import io.schiar.fridgnet.model.Region

interface PolygonsRepository {
    fun currentLocation(): Location?
    suspend fun switchRegion(region: Region, onCurrentLocationChanged: () -> Unit)
    suspend fun switchAll(onCurrentLocationChanged: () -> Unit)
}