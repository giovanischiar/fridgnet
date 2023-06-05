package io.schiar.fridgnet.model.repository

import io.schiar.fridgnet.model.Address
import io.schiar.fridgnet.model.BoundingBox
import io.schiar.fridgnet.model.Location
import io.schiar.fridgnet.model.Region

interface LocationRepository {
    var allCitiesBoundingBox: BoundingBox?
    suspend fun loadRegions(address: Address, onLocationReady: (location: Location) -> Unit)
    fun regionsThatIntersect(boundingBox: BoundingBox): List<Region>
    fun locationFrom(region: Region): Location?

    fun cityAddressNameLocation(): Map<String, Location>

    suspend fun switchRegion(region: Region): Location?
    suspend fun setup()
}