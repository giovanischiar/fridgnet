package io.schiar.fridgnet.library.room

import io.schiar.fridgnet.model.Address
import io.schiar.fridgnet.model.Location
import io.schiar.fridgnet.model.Region
import io.schiar.fridgnet.model.service.LocationService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LocationRoomService(private val locationDAO: LocationDAO) : LocationService {
    override fun retrieve(): Flow<List<Location>> {
        return locationDAO.selectLocationsWithRegions().map { locationsWithRegion ->
            locationsWithRegion.toLocations()
        }
    }

    override fun retrieveRegions(): Flow<List<Region>> {
        return locationDAO.selectRegions().map { it.toRegions() }
    }

    override fun retrieve(region: Region): Flow<Location?> {
        return locationDAO.select(regionID = region.id).map { it?.toLocation() }
    }

    override fun retrieve(address: Address): Flow<Location?> {
        return selectLocationByAddress(address = address)
    }

    fun selectLocationByAddress(address: Address): Flow<Location?> {
        return locationDAO.selectLocationWithRegionsByAddress(
            addressID = address.id
        ).map { it?.toLocation() }
    }

    override suspend fun create(location: Location) { locationDAO.insert(location = location) }

    override suspend fun update(location: Location) {
        locationDAO.update(location = location)
    }
}