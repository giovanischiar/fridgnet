package io.schiar.fridgnet.library.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import io.schiar.fridgnet.Log
import io.schiar.fridgnet.library.room.entity.AdministrativeUnitNameEntity
import io.schiar.fridgnet.library.room.entity.GeoLocationEntity
import io.schiar.fridgnet.library.room.relationentity.AdministrativeUnitNameWithCartographicBoundaries
import io.schiar.fridgnet.model.AdministrativeUnitName
import io.schiar.fridgnet.model.GeoLocation
import kotlinx.coroutines.flow.Flow

/**
 * The class that serves as an interface between the app and the database for CRUD operations
 * (Create, Read, Update, Delete) and retrieval of AdministrativeUnitName data, potentially
 * including related entities.
 */
@Dao
abstract class AdministrativeUnitNameDAO {
    /**
     * Inserts a new [AdministrativeUnitNameEntity] object into the database.
     *
     * This method is intended for Room to handle basic insert operations. For updates to
     * AdministrativeUnitName, use `insertOrUpdate` instead.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(administrativeUnitNameEntity: AdministrativeUnitNameEntity): Long

    /**
     * Inserts a new [GeoLocationEntity] object into the database.
     *
     * This method is intended for Room to handle basic insert operations. For updates to
     * AdministrativeUnitName, use `insertOrUpdate` instead.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(geoLocationEntity: GeoLocationEntity): Long

    /**
     * Inserts a [GeoLocation] along with its associated [AdministrativeUnitName]. This method uses
     * a transaction to ensure data consistency.
     *
     * 1. It calls `insertOrUpdate` to check if a similar AdministrativeUnitName (same locality and
     * admin area) already exists in the database.
     *      - If an existing AdministrativeUnitName is found but its subAdminArea is null, it's updated
     * with the provided subAdminArea.
     *      - If no matching AdministrativeUnitName is found or the existing one has a different
     * subAdminArea, a new AdministrativeUnitName is inserted.
     * 2. Based on the result of `insertOrUpdate` (the AdministrativeUnitName entity ID), a new
     * GeoLocationEntity is created with a foreign key reference to the corresponding
     * AdministrativeUnitNameEntity.
     *
     * @param geoLocation the GeoLocation data to insert.
     * @param administrativeUnitName the AdministrativeUnitName data to insert.
     */
    @Transaction
    open suspend fun insert(
        geoLocation: GeoLocation, administrativeUnitName: AdministrativeUnitName
    ) {
        val administrativeUnitNameEntityID = insertOrUpdate(
            administrativeUnitName = administrativeUnitName
        ) ?: return
        insert(
            geoLocationEntity = geoLocation.toGeoLocationEntity(
                administrativeUnitNameGeoLocationsID = administrativeUnitNameEntityID
            )
        )
    }

    private suspend fun insertOrUpdate(administrativeUnitName: AdministrativeUnitName): Long? {
        val (_, locality, subAdminArea, adminArea) = administrativeUnitName

        val storedAdministrativeUnitNameEntities = selectAdministrativeUnitNamesBy(
            locality = locality ?: return null,
            adminArea = adminArea ?: return null
        )

        if (storedAdministrativeUnitNameEntities.isEmpty()) {
            return insert(
                administrativeUnitNameEntity = administrativeUnitName
                    .toAdministrativeUnitNameEntity()
            )
        }

        if (storedAdministrativeUnitNameEntities.size > 1) {
            val multipleCounties = storedAdministrativeUnitNameEntities.map {
                it.subAdminArea
            }.joinToString(", ")
            val administrativeUnitNameID = storedAdministrativeUnitNameEntities
                .filter { administrativeUnitNameEntity ->
                    administrativeUnitNameEntity.subAdminArea == subAdminArea
                }.getOrNull(index = 0)?.id ?: 0
            return if (administrativeUnitNameID != 0L) {
                administrativeUnitNameID
            } else {
                Log.d(
                    tag = "Store AdministrativeUnitName",
                    msg ="Can't assume if $locality is in one of these counties: " +
                         "$multipleCounties, or $subAdminArea. Create a new AdministrativeUnitName"
                )
                insert(
                    administrativeUnitNameEntity = administrativeUnitName
                        .toAdministrativeUnitNameEntity(id = administrativeUnitNameID)
                )
            }
        }

        val storedAdministrativeUnitNameEntity = storedAdministrativeUnitNameEntities[0]

        if (storedAdministrativeUnitNameEntity.subAdminArea == null && subAdminArea != null) {
            Log.d(
                tag = "Store AdministrativeUnitName",
                msg = "Assuming ${storedAdministrativeUnitNameEntity.locality} is in " +
                      "$subAdminArea. Updating stored administrativeUnitName"
            )
            update(storedAdministrativeUnitNameEntity.updateSubAdminArea(subAdminArea))
            return storedAdministrativeUnitNameEntity.id
        }

        if (subAdminArea == null && storedAdministrativeUnitNameEntity.subAdminArea != null) {
            Log.d(
                tag = "Store AdministrativeUnitName",
                msg = "Assuming ${administrativeUnitName.locality} is in " +
                      "${storedAdministrativeUnitNameEntity.subAdminArea}"
            )
            return storedAdministrativeUnitNameEntity.id
        }

        if (subAdminArea != storedAdministrativeUnitNameEntity.subAdminArea) {
            Log.d(
                tag = "Store AdministrativeUnitName",
                msg = "Can't assume if $locality is in " +
                      "${storedAdministrativeUnitNameEntity.subAdminArea} or $subAdminArea. " +
                      "Inserting a new AdministrativeUnitName"
            )
            return insert(
                administrativeUnitNameEntity = administrativeUnitName
                    .toAdministrativeUnitNameEntity()
            )
        }

        return if (storedAdministrativeUnitNameEntity.id == 0L) {
            null
        } else storedAdministrativeUnitNameEntity.id
    }

    /**
     * Updates an existing [AdministrativeUnitNameEntity] in the database.
     *
     * This method is public for Room usage, but updates to AdministrativeUnitName data are
     * recommended to be done through `insertOrUpdate` to handle potential duplicates.
     */
    @Update
    abstract suspend fun update(administrativeUnitNameEntity: AdministrativeUnitNameEntity)

    /**
     * Returns a flow of lists containing information about Administrative Units with their
     * Cartographic Boundaries.
     *
     * This function retrieves data asynchronously using a Flow. Each item in the flow is a list of
     * custom objects that combine the Administrative Unit name with its associated Cartographic
     * Boundary data.
     *
     * @return a Flow of lists containing `AdministrativeUnitWithBoundary` objects.
     */
    @Query("SELECT * FROM AdministrativeUnitName")
    abstract fun selectAdministrativeUnitNameWithCartographicBoundaries()
        : Flow<List<AdministrativeUnitNameWithCartographicBoundaries>>

    /**
     * Returns a list of AdministrativeUnitName entities that share the same locality and adminArea.
     *
     * This query is typically used to find potential duplicates or variations in sub-administrative
     * area data. It retrieves entries where the 'locality' and 'adminArea' fields match the
     * specified parameters, potentially including AdministrativeUnitNames with different
     * sub-administrative areas or even null sub-administrative areas.
     *
     * @param locality the locality name to match.
     * @param adminArea the adminArea name to match.
     * @return a list of AdministrativeUnitNameEntity objects matching the criteria.
     */
    @Query(
        "SELECT * " +
        "FROM AdministrativeUnitName " +
        "WHERE AdministrativeUnitName.locality IS :locality AND " +
              "AdministrativeUnitName.adminArea = :adminArea"
    )
    abstract suspend fun selectAdministrativeUnitNamesBy(
        locality: String, adminArea: String
    ): List<AdministrativeUnitNameEntity>
}