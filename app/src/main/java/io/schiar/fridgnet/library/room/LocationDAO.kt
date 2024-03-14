package io.schiar.fridgnet.library.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import io.schiar.fridgnet.library.room.entity.CoordinateEntity
import io.schiar.fridgnet.library.room.entity.LocationEntity
import io.schiar.fridgnet.library.room.entity.PolygonEntity
import io.schiar.fridgnet.library.room.entity.RegionEntity
import io.schiar.fridgnet.library.room.relationentity.LocationWithRegions
import io.schiar.fridgnet.library.room.relationentity.RegionWithPolygonAndHoles
import io.schiar.fridgnet.model.Coordinate
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
    abstract suspend fun insertCoordinates(coordinateEntities: List<CoordinateEntity>): List<Long>

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
        insertCoordinates(coordinatesID = polygonID, coordinates = polygon.coordinates)
        return polygonID
    }

    private suspend fun insertHoles(regionID: Long, holes: List<Polygon>) {
        for (hole in holes) {
            val holeID = insert(polygonEntity = PolygonEntity(holesID = regionID))
            insertCoordinates(coordinatesID = holeID, coordinates = hole.coordinates)
        }
    }

    private suspend fun insertCoordinates(coordinatesID: Long, coordinates: List<Coordinate>) {
        val coordinateEntities = coordinates.toCoordinateEntities(coordinatesID = coordinatesID)
        insertCoordinates(coordinateEntities)
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
        updateCoordinates(coordinatesID = polygon.id, coordinates = polygon.coordinates)
    }

    private suspend fun updateHoles(regionID: Long, holes: List<Polygon>) {
        for (hole in holes) {
            val polygonEntity = PolygonEntity(id = hole.id, holesID = regionID)
            update(polygonEntity = polygonEntity)
            updateCoordinates(coordinatesID = polygonEntity.id, coordinates = hole.coordinates)
        }
    }

    private suspend fun updateCoordinates(coordinatesID: Long, coordinates: List<Coordinate>) {
        val coordinateEntities = coordinates.toCoordinateEntitiesWithID(
            coordinatesID = coordinatesID
        )
        updateCoordinates(coordinateEntities)
    }

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
    abstract suspend fun updateCoordinates(coordinateEntities: List<CoordinateEntity>)

    @Query(
        "SELECT * FROM Location WHERE " +
                "Location.locality is :locality AND " +
                "Location.subAdminArea is :subAdminArea AND " +
                "Location.adminArea is :adminArea AND " +
                "Location.countryName is :countryName "
    )
    abstract fun selectLocationWithRegionsByAddress(
        locality: String?,
        subAdminArea: String?,
        adminArea: String?,
        countryName: String?
    ): Flow<LocationWithRegions?>
}