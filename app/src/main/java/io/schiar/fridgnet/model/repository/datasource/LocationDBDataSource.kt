package io.schiar.fridgnet.model.repository.datasource

import io.schiar.fridgnet.model.*
import io.schiar.fridgnet.model.repository.datasource.room.LocationDatabase
import io.schiar.fridgnet.model.repository.datasource.room.entity.PolygonEntity
import io.schiar.fridgnet.model.repository.datasource.util.toCoordinateEntities
import io.schiar.fridgnet.model.repository.datasource.util.toLocation
import io.schiar.fridgnet.model.repository.datasource.util.toLocationEntity
import io.schiar.fridgnet.model.repository.datasource.util.toRegionEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LocationDBDataSource(locationDatabase: LocationDatabase): LocationDataSource {
    private var locationDAO = locationDatabase.locationDAO()
    private var addressLocation: Map<Address, Location> = emptyMap()

    suspend fun setup() {
        coroutineScope {
            launch {
                withContext(Dispatchers.IO) { selectLocations() }.forEach { location ->
                    addressLocation = addressLocation + (location.address to location)
                }
            }
        }
    }

    fun insert(location: Location) {
        addressLocation = addressLocation + (location.address to location)
        insertLocation(location = location)
    }

    fun selectLocationByAddress(address: Address): Location {
        val (locality, subAdminArea, adminArea, countryName) = address
        return locationDAO.selectLocationWithRegionsByAddress(
            locality = locality,
            subAdminArea = subAdminArea,
            adminArea = adminArea,
            countryName = countryName
        ).toLocation()
    }

    private fun insertLocation(location: Location) {
        val locationID = locationDAO.insert(locationEntity = location.toLocationEntity())
        insertRegions(locationID = locationID, regions = location.regions)
    }

    private fun insertRegions(locationID: Long, regions: List<Region>) {
        regions.forEach { region ->
            insertRegion(locationID = locationID, region = region)
        }
    }

    private fun insertRegion(locationID: Long, region: Region) {
        val (polygon, holes, _) = region
        val polygonID = insertPolygon(polygon)
        val regionEntity = region.toRegionEntity(regionsID = locationID, polygonID = polygonID)
        val regionID = locationDAO.insert(regionEntity = regionEntity)
        insertHoles(regionID = regionID, holes)
    }

    private fun insertPolygon(polygon: Polygon): Long {
        val polygonID = locationDAO.insert(polygonEntity = PolygonEntity())
        insertCoordinates(coordinatesID = polygonID, coordinates = polygon.coordinates)
        return polygonID
    }

    private fun insertHoles(regionID: Long, holes: List<Polygon>) {
        for (hole in holes) {
            val holeID = locationDAO.insert(polygonEntity = PolygonEntity(holesID = regionID))
            insertCoordinates(coordinatesID = holeID, coordinates = hole.coordinates)
        }
    }

    private fun insertCoordinates(coordinatesID: Long, coordinates: List<Coordinate>) {
        val coordinateEntities = coordinates.toCoordinateEntities(coordinatesID = coordinatesID)
        locationDAO.insertCoordinates(coordinateEntities)
    }

    private fun selectLocations(): List<Location> {
        return locationDAO.selectLocationsWithRegions().map { it.toLocation() }
    }

    override suspend fun fetchCity(address: Address): Location? {
        return addressLocation[address]
    }

    override suspend fun fetchCounty(address: Address): Location? {
        return addressLocation[address]
    }

    override suspend fun fetchState(address: Address): Location? {
        return addressLocation[address]
    }

    override suspend fun fetchCountry(address: Address): Location? {
        return addressLocation[address]
    }
}