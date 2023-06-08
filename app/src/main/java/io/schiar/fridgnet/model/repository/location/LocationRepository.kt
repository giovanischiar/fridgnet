package io.schiar.fridgnet.model.repository.location

import io.schiar.fridgnet.model.Address
import io.schiar.fridgnet.model.BoundingBox
import io.schiar.fridgnet.model.Location
import io.schiar.fridgnet.model.Region

interface LocationRepository {
    var allCitiesBoundingBox: BoundingBox?
    var currentLocation: Location?
    suspend fun loadRegions(address: Address, onLocationReady: (location: Location) -> Unit)
    fun regionsThatIntersect(boundingBox: BoundingBox): List<Region>
    fun selectNewLocationFrom(region: Region)

    fun cityAddressNameLocation(): Map<String, Location>

    suspend fun switchRegion(region: Region)
    suspend fun switchAll()
    suspend fun setup()
}