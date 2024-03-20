package io.schiar.fridgnet.library.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import io.schiar.fridgnet.library.room.entity.CartographicBoundaryEntity
import io.schiar.fridgnet.library.room.entity.GeoLocationEntity
import io.schiar.fridgnet.library.room.entity.PolygonEntity
import io.schiar.fridgnet.library.room.entity.RegionEntity
import io.schiar.fridgnet.library.room.relationentity.CartographicBoundaryWithRegions
import io.schiar.fridgnet.library.room.relationentity.RegionWithPolygonAndHoles
import io.schiar.fridgnet.model.CartographicBoundary
import io.schiar.fridgnet.model.GeoLocation
import io.schiar.fridgnet.model.Polygon
import io.schiar.fridgnet.model.Region
import kotlinx.coroutines.flow.Flow

@Dao
abstract class CartographicBoundaryDAO {
    @Insert(onConflict = REPLACE)
    abstract suspend fun insert(cartographicBoundaryEntity: CartographicBoundaryEntity): Long

    @Insert(onConflict = REPLACE)
    abstract suspend fun insert(regionEntity: RegionEntity): Long

    @Insert(onConflict = REPLACE)
    abstract suspend fun insert(polygonEntity: PolygonEntity): Long

    @Insert(onConflict = REPLACE)
    abstract suspend fun insertGeoLocations(
        geoLocationEntities: List<GeoLocationEntity>
    ): List<Long>

    @Transaction
    open suspend fun insert(cartographicBoundary: CartographicBoundary) {
        val cartographicBoundaryID = insert(
            cartographicBoundaryEntity = cartographicBoundary.toCartographicBoundaryEntity()
        )
        insertRegions(
            cartographicBoundaryID = cartographicBoundaryID,
            regions = cartographicBoundary.regions
        )
    }

    private suspend fun insertRegions(cartographicBoundaryID: Long, regions: List<Region>) {
        regions.forEach { region ->
            insertRegion(cartographicBoundaryID = cartographicBoundaryID, region = region)
        }
    }

    private suspend fun insertRegion(cartographicBoundaryID: Long, region: Region) {
        val (_, polygon, holes, _) = region
        val polygonID = insertPolygon(polygon)
        val regionEntity = region.toRegionEntity(
            regionsID = cartographicBoundaryID, polygonID = polygonID
        )
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
    open suspend fun update(cartographicBoundary: CartographicBoundary) {
        update(
            cartographicBoundaryEntity = cartographicBoundary.toCartographicBoundaryEntity(
                id = cartographicBoundary.id
            )
        )
        updateRegions(
            cartographicBoundaryID = cartographicBoundary.id, regions = cartographicBoundary.regions
        )
    }

    private suspend fun updateRegions(cartographicBoundaryID: Long, regions: List<Region>) {
        regions.forEach { region ->
            updateRegion(cartographicBoundaryID = cartographicBoundaryID, region = region)
        }
    }

    private suspend fun updateRegion(cartographicBoundaryID: Long, region: Region) {
        val (id, polygon, holes) = region
        updatePolygon(polygon)
        val regionEntity = region.toRegionEntity(
            id = id,
            regionsID = cartographicBoundaryID,
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

    @Query(
        "SELECT * " +
        "FROM CartographicBoundary " +
        "WHERE administrativeUnitNameCartographicBoundariesID = :administrativeUnitNameID"
    )
    abstract fun selectCartographicBoundaryWithRegionsByAdministrativeUnitName(
        administrativeUnitNameID: Long
    ): Flow<CartographicBoundaryWithRegions?>

    @Query(
        "SELECT * " +
        "FROM CartographicBoundary " +
        "WHERE id IN (" +
                "SELECT regionsID " +
                "FROM Region " +
                "WHERE id = :regionID" +
              ")"
    )
    abstract fun select(regionID: Long): Flow<CartographicBoundaryWithRegions?>

    @Transaction
    @Query("SELECT * FROM CartographicBoundary")
    abstract fun selectCartographicBoundariesWithRegions()
        : Flow<List<CartographicBoundaryWithRegions>>

    @Query("SELECT * FROM Region")
    abstract fun selectRegions(): Flow<List<RegionWithPolygonAndHoles>>

    @Update
    abstract suspend fun update(cartographicBoundaryEntity: CartographicBoundaryEntity)

    @Update
    abstract suspend fun update(regionEntity: RegionEntity)

    @Update
    abstract suspend fun update(polygonEntity: PolygonEntity)

    @Update
    abstract suspend fun updateGeoLocations(geoLocationEntities: List<GeoLocationEntity>)
}