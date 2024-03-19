package io.schiar.fridgnet.library.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import io.schiar.fridgnet.library.room.entity.GeoLocationEntity
import io.schiar.fridgnet.library.room.entity.LocationEntity
import io.schiar.fridgnet.library.room.entity.PolygonEntity
import io.schiar.fridgnet.library.room.entity.RegionEntity
import io.schiar.fridgnet.library.room.relationentity.LocationWithRegions
import io.schiar.fridgnet.library.room.relationentity.RegionWithPolygonAndHoles
import io.schiar.fridgnet.model.GeoLocation
import io.schiar.fridgnet.model.Location
import io.schiar.fridgnet.model.Polygon
import io.schiar.fridgnet.model.Region
import kotlinx.coroutines.flow.Flow

@Dao
abstract class LocationDAO {
    @Insert(onConflict = REPLACE)
    abstract suspend fun insert(locationEntity: LocationEntity): Long

    @Insert(onConflict = REPLACE)
    abstract suspend fun insert(regionEntity: RegionEntity): Long

    @Insert(onConflict = REPLACE)
    abstract suspend fun insert(polygonEntity: PolygonEntity): Long

    @Insert(onConflict = REPLACE)
    abstract suspend fun insertGeoLocations(geoLocationEntities: List<GeoLocationEntity>): List<Long>

    @Transaction
    open suspend fun insert(location: Location) {
        val locationID = insert(locationEntity = location.toLocationEntity())
        insertRegions(locationID = locationID, regions = location.regions)
    }

    private suspend fun insertRegions(locationID: Long, regions: List<Region>) {
        regions.forEach { region ->
            insertRegion(locationID = locationID, region = region)
        }
    }

    private suspend fun insertRegion(locationID: Long, region: Region) {
        val (_, polygon, holes, _) = region
        val polygonID = insertPolygon(polygon)
        val regionEntity = region.toRegionEntity(regionsID = locationID, polygonID = polygonID)
        val regionID = insert(regionEntity = regionEntity)
        insertHoles(regionID = regionID, holes)
    }

    private suspend fun insertPolygon(polygon: Polygon): Long {
        val polygonID = insert(polygonEntity = PolygonEntity())
        insertGeoLocations(geoLocationsID = polygonID, geoLocations = polygon.geoLocations)
        return polygonID
    }

    private suspend fun insertHoles(regionID: Long, holes: List<Polygon>) {
        for (hole in holes) {
            val holeID = insert(polygonEntity = PolygonEntity(holesID = regionID))
            insertGeoLocations(geoLocationsID = holeID, geoLocations = hole.geoLocations)
        }
    }

    private suspend fun insertGeoLocations(geoLocationsID: Long, geoLocations: List<GeoLocation>) {
        val geoLocationEntities = geoLocations.toGeoLocationEntities(
            geoLocationsID = geoLocationsID
        )
        insertGeoLocations(geoLocationEntities)
    }

    @Transaction
    open suspend fun update(location: Location) {
        update(locationEntity = location.toLocationEntity(id = location.id))
        updateRegions(locationID = location.id, regions = location.regions)
    }

    private suspend fun updateRegions(locationID: Long, regions: List<Region>) {
        regions.forEach { region ->
            updateRegion(locationID = locationID, region = region)
        }
    }

    private suspend fun updateRegion(locationID: Long, region: Region) {
        val (id, polygon, holes) = region
        updatePolygon(polygon)
        val regionEntity = region.toRegionEntity(
            id = id,
            regionsID = locationID,
            polygonID = polygon.id
        )
        update(regionEntity = regionEntity)
        updateHoles(regionID = region.id, holes)
    }

    private suspend fun updatePolygon(polygon: Polygon) {
        update(polygonEntity = PolygonEntity(id = polygon.id))
        updateGeoLocations(geoLocationsID = polygon.id, geoLocations = polygon.geoLocations)
    }

    private suspend fun updateHoles(regionID: Long, holes: List<Polygon>) {
        for (hole in holes) {
            val polygonEntity = PolygonEntity(id = hole.id, holesID = regionID)
            update(polygonEntity = polygonEntity)
            updateGeoLocations(geoLocationsID = polygonEntity.id, geoLocations = hole.geoLocations)
        }
    }

    private suspend fun updateGeoLocations(geoLocationsID: Long, geoLocations: List<GeoLocation>) {
        val getLocationEntities = geoLocations.toGeoLocationEntitiesWithID(
            geoLocationsID = geoLocationsID
        )
        updateGeoLocations(getLocationEntities)
    }

    @Query("SELECT * FROM Location Where addressLocationsID = :addressID")
    abstract fun selectLocationWithRegionsByAddress(addressID: Long): Flow<LocationWithRegions?>

    @Query("SELECT * FROM Location WHERE id IN (SELECT regionsID FROM Region WHERE id = :regionID)")
    abstract fun select(regionID: Long): Flow<LocationWithRegions?>

    @Transaction
    @Query("SELECT * FROM Location")
    abstract fun selectLocationsWithRegions(): Flow<List<LocationWithRegions>>

    @Query("SELECT * FROM Region")
    abstract fun selectRegions(): Flow<List<RegionWithPolygonAndHoles>>

    @Update
    abstract suspend fun update(locationEntity: LocationEntity)

    @Update
    abstract suspend fun update(regionEntity: RegionEntity)

    @Update
    abstract suspend fun update(polygonEntity: PolygonEntity)

    @Update
    abstract suspend fun updateGeoLocations(geoLocationEntities: List<GeoLocationEntity>)
}