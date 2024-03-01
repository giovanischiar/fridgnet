package io.schiar.fridgnet.library.room

import io.schiar.fridgnet.Log
import io.schiar.fridgnet.library.room.entity.PolygonEntity
import io.schiar.fridgnet.library.room.relationentity.RegionWithPolygonAndHoles
import io.schiar.fridgnet.model.Address
import io.schiar.fridgnet.model.Coordinate
import io.schiar.fridgnet.model.Location
import io.schiar.fridgnet.model.Polygon
import io.schiar.fridgnet.model.Region
import io.schiar.fridgnet.model.datasource.LocationDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LocationRoomDataSource(private val locationDAO: LocationDAO) : LocationDataSource {
    override fun retrieve(): Flow<List<Location>> {
        return locationDAO.selectLocationsWithRegions().map { locationsWithRegion ->
            locationsWithRegion.toLocations()
        }
    }

    override suspend fun retrieve(address: Address): Location? {
        return selectLocationByAddress(address = address)
    }

    suspend fun selectLocationByAddress(address: Address): Location? {
        val (locality, subAdminArea, adminArea, countryName) = address
        return locationDAO.selectLocationWithRegionsByAddress(
            locality = locality,
            subAdminArea = subAdminArea,
            adminArea = adminArea,
            countryName = countryName
        )?.toLocation()
    }

    override suspend fun create(location: Location) {
        val locationID = locationDAO.insert(locationEntity = location.toLocationEntity())
        insertRegions(locationID = locationID, regions = location.regions)
    }

    private suspend fun update(location: Location): List<RegionWithPolygonAndHoles>? {
        val (locality, subAdminArea, adminArea, countryName) = location.address
        val locationWithRegions = locationDAO.selectLocationWithRegionsByAddress(
            locality = locality,
            subAdminArea = subAdminArea,
            adminArea = adminArea,
            countryName = countryName
        ) ?: return null
        val locationEntity = locationWithRegions.locationEntity
        locationDAO.update(
            locationEntity.boundingBoxUpdated(
                southwestLatitude = location.boundingBox.southwest.latitude,
                southwestLongitude = location.boundingBox.southwest.longitude,
                northeastLatitude = location.boundingBox.northeast.latitude,
                northeastLongitude = location.boundingBox.northeast.longitude
            )
        )
        return locationWithRegions.regions
    }

    override suspend fun updateWithRegionSwitched(location: Location, region: Region) {
        val regionsWithPolygonAndHoles = update(location = location) ?: return
        val regionEntity = regionsWithPolygonAndHoles.find {
            it.toRegion().polygon == region.polygon
        }?.regionEntity ?: return
        locationDAO.update(regionEntity = regionEntity.switch())
    }

    override suspend fun updateWithAllRegionsSwitched(location: Location) {
        val regionsWithPolygonAndHoles = update(location = location) ?: return
        val regionEntities = regionsWithPolygonAndHoles.sortedByDescending {
            it.polygon.coordinates.size
        }.map { it.regionEntity }

        regionEntities.subList(1, regionEntities.size).forEach { regionEntity ->
            Log.d("LocationDBDataSource", "|${Thread.currentThread().name}|Updating Region")
            locationDAO.update(regionEntity = regionEntity.switch())
            Log.d("LocationDBDataSource", "|${Thread.currentThread().name}|Region Updated!")
        }
    }

    private suspend fun insertRegions(locationID: Long, regions: List<Region>) {
        regions.forEach { region ->
            insertRegion(locationID = locationID, region = region)
        }
    }

    private suspend fun insertRegion(locationID: Long, region: Region) {
        val (polygon, holes, _) = region
        val polygonID = insertPolygon(polygon)
        val regionEntity = region.toRegionEntity(regionsID = locationID, polygonID = polygonID)
        val regionID = locationDAO.insert(regionEntity = regionEntity)
        insertHoles(regionID = regionID, holes)
    }

    private suspend fun insertPolygon(polygon: Polygon): Long {
        val polygonID = locationDAO.insert(polygonEntity = PolygonEntity())
        insertCoordinates(coordinatesID = polygonID, coordinates = polygon.coordinates)
        return polygonID
    }

    private suspend fun insertHoles(regionID: Long, holes: List<Polygon>) {
        for (hole in holes) {
            val holeID = locationDAO.insert(polygonEntity = PolygonEntity(holesID = regionID))
            insertCoordinates(coordinatesID = holeID, coordinates = hole.coordinates)
        }
    }

    private suspend fun insertCoordinates(coordinatesID: Long, coordinates: List<Coordinate>) {
        val coordinateEntities = coordinates.toCoordinateEntities(coordinatesID = coordinatesID)
        locationDAO.insertCoordinates(coordinateEntities)
    }
}