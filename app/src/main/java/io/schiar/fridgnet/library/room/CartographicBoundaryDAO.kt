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
import io.schiar.fridgnet.model.CartographicBoundary
import io.schiar.fridgnet.model.GeoLocation
import io.schiar.fridgnet.model.Polygon
import io.schiar.fridgnet.model.Region
import kotlinx.coroutines.flow.Flow

/**
 * The class that serves as an interface between the app and the database for CRUD operations
 * (Create, Read, Update, Delete) and retrieval of CartographicBoundary data, potentially
 * including related entities.
 */
@Dao
abstract class CartographicBoundaryDAO {
    /**
     * Insert a [CartographicBoundaryEntity] into the database.
     *
     * This method is intended for Room to handle basic insert operations. For insert a
     * [CartographicBoundary], use `insert(cartographicBoundary: CartographicBoundary)` instead.
     */
    @Insert(onConflict = REPLACE)
    abstract suspend fun insert(cartographicBoundaryEntity: CartographicBoundaryEntity): Long

    /**
     * Insert a [RegionEntity] into the database
     *
     * This method is intended for Room to handle basic insert operations. For insert a
     * [CartographicBoundary], use `insert(cartographicBoundary: CartographicBoundary)` instead.
     */
    @Insert(onConflict = REPLACE)
    abstract suspend fun insert(regionEntity: RegionEntity): Long

    /**
     * Insert a [PolygonEntity] into the database
     *
     * This method is intended for Room to handle basic insert operations. For insert a
     * [CartographicBoundary], use `insert(cartographicBoundary: CartographicBoundary)` instead.
     */
    @Insert(onConflict = REPLACE)
    abstract suspend fun insert(polygonEntity: PolygonEntity): Long

    /**
     * Insert a [GeoLocationEntity] into the database
     *
     * This method is intended for Room to handle basic insert operations. For insert a
     * [CartographicBoundary], use `insert(cartographicBoundary: CartographicBoundary)` instead.
     */
    @Insert(onConflict = REPLACE)
    abstract suspend fun insertGeoLocations(
        geoLocationEntities: List<GeoLocationEntity>
    ): List<Long>

    /**
     * Inserts a [CartographicBoundary] object and its associated regions into the database using a
     * transaction.
     *
     * This method first converts the CartographicBoundary object to a CartographicBoundaryEntity
     * and inserts it into the database. It then retrieves the generated ID and uses it as a foreign
     * key to insert the associated regions using a separate method (`insertRegions`).
     *
     * @param cartographicBoundary the CartographicBoundary object to insert along with its
     * associated regions.
     */
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

    /**
     * Updates a [CartographicBoundary] object and its associated regions in the database using a
     * transaction.
     *
     * This method first converts the CartographicBoundary object to a CartographicBoundaryEntity
     * and updates the database entry identified by the CartographicBoundary's ID. It then uses the
     * same ID as a foreign key to update the associated regions using a separate method
     * (`updateRegions`).
     *
     * @param cartographicBoundary the CartographicBoundary object with updated data and its
     * associated regions.
     */
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

    /**
     * Retrieves a [Flow] of [CartographicBoundaryEntity] objects associated with a specific
     * AdministrativeUnitName identified by its ID.
     *
     * This method utilizes a Flow to emit data asynchronously. The Flow might emit null if no
     * matching CartographicBoundaryEntity is found for the provided administrativeUnitNameID.
     *
     * @param administrativeUnitNameID the ID of the AdministrativeUnitName to retrieve associated
     * boundaries for.
     * @return a [Flow] of CartographicBoundaryEntity objects or null if no match is found.
     */
    @Query(
        "SELECT * " +
        "FROM CartographicBoundary " +
        "WHERE administrativeUnitNameCartographicBoundariesID = :administrativeUnitNameID"
    )
    abstract fun selectCartographicBoundaryWithRegionsByAdministrativeUnitName(
        administrativeUnitNameID: Long
    ): Flow<CartographicBoundaryWithRegions?>

    /**
     * Retrieves a [Flow] of [CartographicBoundaryEntity] objects associated with a specific
     * Region identified by its ID.
     *
     * @param regionID the id of the Region that belongs the the [CartographicBoundaryEntity]
     * @return the [Flow] of [CartographicBoundaryWithRegions] objects or null if no match is found.
     */
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

    /**
     * retrieves all the CartographicBoundaryEntities along with their regions
     *
     * @return all the CartographicBoundaryEntities along with their regions
     */
    @Transaction
    @Query("SELECT * FROM CartographicBoundary")
    abstract fun selectCartographicBoundariesWithRegions()
        : Flow<List<CartographicBoundaryWithRegions>>

    /**
     * Update a [CartographicBoundaryEntity] into the database
     *
     * This method is intended for Room to handle basic update operations. For update a
     * [CartographicBoundary], use `update(cartographicBoundary: CartographicBoundary)` instead.
     */
    @Update
    abstract suspend fun update(cartographicBoundaryEntity: CartographicBoundaryEntity)

    /**
     * Update a [RegionEntity] in the database
     *
     * This method is intended for Room to handle basic insert operations. For update a
     * [CartographicBoundary], use `update(cartographicBoundary: CartographicBoundary)` instead.
     */
    @Update
    abstract suspend fun update(regionEntity: RegionEntity)

    /**
     * Update a [PolygonEntity] in the database
     *
     * This method is intended for Room to handle basic insert operations. For update a
     * [CartographicBoundary], use `update(cartographicBoundary: CartographicBoundary)` instead.
     */
    @Update
    abstract suspend fun update(polygonEntity: PolygonEntity)

    /**
     * Update the [GeoLocations] in the database
     *
     * This method is intended for Room to handle basic insert operations. For update a
     * [CartographicBoundary], use `update(cartographicBoundary: CartographicBoundary)` instead.
     */
    @Update
    abstract suspend fun updateGeoLocations(geoLocationEntities: List<GeoLocationEntity>)
}