package io.schiar.fridgnet.model.repository.location.datasource

import android.util.Log
import io.schiar.fridgnet.model.*
import io.schiar.fridgnet.model.repository.location.datasource.room.LocationDatabase
import io.schiar.fridgnet.model.repository.location.datasource.room.entity.PolygonEntity
import io.schiar.fridgnet.model.repository.location.datasource.room.relationentity.RegionWithPolygonAndHoles
import io.schiar.fridgnet.model.repository.location.datasource.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LocationDBDataSource(locationDatabase: LocationDatabase): LocationDataSource {
    private var locationDAO = locationDatabase.locationDAO()

    suspend fun setup(onLoaded: (location: Location) -> Unit) = coroutineScope {
        launch {
            withContext(Dispatchers.IO) { selectLocations() }.forEach { location ->
                onLoaded(location)
            }
        }
    }

    private fun selectLocations(): List<Location> {
        return locationDAO.selectLocationsWithRegions().map { locationWithRegion ->
            locationWithRegion.toLocation()
        }
    }

    override suspend fun fetchLocationBy(address: Address): Location? {
        return selectLocationByAddress(address = address)
    }

    fun selectLocationByAddress(address: Address): Location? {
        val (locality, subAdminArea, adminArea, countryName) = address
        return locationDAO.selectLocationWithRegionsByAddress(
            locality = locality,
            subAdminArea = subAdminArea,
            adminArea = adminArea,
            countryName = countryName
        )?.toLocation()
    }

    fun insert(location: Location) {
        val locationID = locationDAO.insert(locationEntity = location.toLocationEntity())
        insertRegions(locationID = locationID, regions = location.regions)
    }

    private fun update(location: Location): List<RegionWithPolygonAndHoles>? {
        val (locality, subAdminArea, adminArea, countryName) = location.address
        val locationWithRegions = synchronized(this) {
            locationDAO.selectLocationWithRegionsByAddress(
                locality = locality,
                subAdminArea = subAdminArea,
                adminArea = adminArea,
                countryName = countryName
            ) ?: return null
        }
        val locationEntity = locationWithRegions.locationEntity
        locationDAO.update(locationEntity.boundingBoxUpdated(
            southwestLatitude = location.boundingBox.southwest.latitude,
            southwestLongitude = location.boundingBox.southwest.longitude,
            northeastLatitude = location.boundingBox.northeast.latitude,
            northeastLongitude = location.boundingBox.northeast.longitude
        ))
        return locationWithRegions.regions
    }

    fun updateLocationWithRegionSwitched(location: Location, region: Region) {
        val regionsWithPolygonAndHoles = update(location = location) ?: return
        val regionEntity = regionsWithPolygonAndHoles.find {
            it.toRegion().polygon == region.polygon
        }?.regionEntity ?: return
        locationDAO.update(regionEntity = regionEntity.switch())
    }

    suspend fun updateLocationWithAllRegionsSwitched(location: Location) = coroutineScope {
        val regionsWithPolygonAndHoles = update(location = location) ?: return@coroutineScope
        val regionEntities = regionsWithPolygonAndHoles.sortedByDescending {
            it.polygon.coordinates.size
        }.map { it.regionEntity }

        regionEntities.subList(1, regionEntities.size).forEach { regionEntity ->
            launch(Dispatchers.IO) {
                Log.d("LocationDBDataSource", "|${Thread.currentThread().name}|Updating Region")
                locationDAO.update(regionEntity = regionEntity.switch())
                Log.d("LocationDBDataSource", "|${Thread.currentThread().name}|Region Updated!")
            }
        }
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
}