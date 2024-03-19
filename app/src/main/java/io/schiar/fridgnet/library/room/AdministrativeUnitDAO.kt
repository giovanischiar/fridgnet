package io.schiar.fridgnet.library.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import io.schiar.fridgnet.Log
import io.schiar.fridgnet.library.room.entity.AdministrativeUnitEntity
import io.schiar.fridgnet.library.room.entity.GeoLocationEntity
import io.schiar.fridgnet.library.room.relationentity.AdministrativeUnitWithCartographicBoundariesAndGeoLocations
import io.schiar.fridgnet.model.AdministrativeUnit
import io.schiar.fridgnet.model.GeoLocation
import kotlinx.coroutines.flow.Flow

@Dao
abstract class AdministrativeUnitDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(administrativeUnitEntity: AdministrativeUnitEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(geoLocationEntity: GeoLocationEntity): Long

    @Transaction
    open suspend fun insert(geoLocation: GeoLocation, administrativeUnit: AdministrativeUnit) {
        val administrativeUnitEntityID = insertOrUpdate(
            administrativeUnit = administrativeUnit
        ) ?: return
        insert(
            geoLocationEntity = geoLocation.toGeoLocationEntity(
                administrativeUnitGeoLocationsID = administrativeUnitEntityID
            )
        )
    }

    @Query(
        "SELECT * FROM GeoLocation " +
        "WHERE administrativeUnitGeoLocationsID = (" +
                    "SELECT id " +
                    "FROM AdministrativeUnit " +
                    "WHERE locality = :locality AND " +
                          "subAdminArea = :subAdminArea AND " +
                          "adminArea = :adminArea AND " +
                          "countryName = :countryName " +
                    "LIMIT 1" +
              ")"
    )
    abstract fun selectGeoLocations(
        locality: String?,
        subAdminArea: String?,
        adminArea: String?,
        countryName: String?
    ): Flow<List<GeoLocationEntity>>

    @Query(
        "SELECT * " +
        "FROM GeoLocation " +
        "WHERE administrativeUnitGeoLocationsID IN (" +
                "SELECT id " +
                "FROM AdministrativeUnit " +
                "WHERE subAdminArea = :subAdminArea AND " +
                      "adminArea = :adminArea AND " +
                      "countryName = :countryName" +
              ")"
    )
    abstract fun selectGeoLocations(
        subAdminArea: String?,
        adminArea: String?,
        countryName: String?
    ): Flow<List<GeoLocationEntity>>

    @Query(
        "SELECT * " +
        "FROM GeoLocation " +
        "WHERE administrativeUnitGeoLocationsID IN (" +
                "SELECT id " +
                "FROM AdministrativeUnit " +
                "WHERE adminArea = :adminArea AND " +
                      "countryName = :countryName" +
              ")"
    )
    abstract fun selectGeoLocations(
        adminArea: String?,
        countryName: String?
    ): Flow<List<GeoLocationEntity>>

    @Query(
        "SELECT * " +
        "FROM GeoLocation " +
        "WHERE administrativeUnitGeoLocationsID IN (" +
                "SELECT id " +
                "FROM AdministrativeUnit " +
                "WHERE countryName = :countryName" +
              ")"
    )
    abstract fun selectGeoLocations(countryName: String?): Flow<List<GeoLocationEntity>>

    private suspend fun insertOrUpdate(administrativeUnit: AdministrativeUnit): Long? {
        val (_, locality, subAdminArea, adminArea) = administrativeUnit

        val storedAdministrativeUnitEntities = selectAdministrativeUnitsBy(
            locality = locality ?: return null,
            adminArea = adminArea ?: return null
        )

        if (storedAdministrativeUnitEntities.isEmpty()) {
            return insert(
                administrativeUnitEntity = administrativeUnit.toAdministrativeUnitEntity()
            )
        }

        if (storedAdministrativeUnitEntities.size > 1) {
            val multipleCounties = storedAdministrativeUnitEntities.map {
                it.subAdminArea
            }.joinToString(", ")
            val administrativeUnitID = storedAdministrativeUnitEntities
                .filter { administrativeUnitEntity ->
                    administrativeUnitEntity.subAdminArea == subAdminArea
                }.getOrNull(index = 0)?.id ?: 0
            return if (administrativeUnitID != 0L) {
                administrativeUnitID
            } else {
                Log.d(
                    tag = "Store AdministrativeUnit",
                    msg ="Can't assume if $locality is in one of these counties: " +
                         "$multipleCounties, or $subAdminArea. Create a new AdministrativeUnit"
                )
                insert(
                    administrativeUnitEntity = administrativeUnit.toAdministrativeUnitEntity(
                        id = administrativeUnitID
                    )
                )
            }
        }

        val storedAdministrativeUnitEntity = storedAdministrativeUnitEntities[0]

        if (storedAdministrativeUnitEntity.subAdminArea == null && subAdminArea != null) {
            Log.d(
                tag = "Store AdministrativeUnit",
                msg = "Assuming ${storedAdministrativeUnitEntity.locality} is in $subAdminArea. " +
                      "Updating stored administrativeUnit"
            )
            update(storedAdministrativeUnitEntity.updateSubAdminArea(subAdminArea))
            return storedAdministrativeUnitEntity.id
        }

        if (subAdminArea == null && storedAdministrativeUnitEntity.subAdminArea != null) {
            Log.d(
                tag = "Store AdministrativeUnit",
                msg = "Assuming ${administrativeUnit.locality} is in " +
                      "${storedAdministrativeUnitEntity.subAdminArea}"
            )
            return storedAdministrativeUnitEntity.id
        }

        if (subAdminArea != storedAdministrativeUnitEntity.subAdminArea) {
            Log.d(
                tag = "Store AdministrativeUnit",
                msg = "Can't assume if $locality is in " +
                      "${storedAdministrativeUnitEntity.subAdminArea} or $subAdminArea. " +
                      "Inserting a new AdministrativeUnit"
            )
            return insert(
                administrativeUnitEntity = administrativeUnit.toAdministrativeUnitEntity()
            )
        }

        return if (storedAdministrativeUnitEntity.id == 0L) {
            null
        } else storedAdministrativeUnitEntity.id
    }

    @Update
    abstract suspend fun update(administrativeUnitEntity: AdministrativeUnitEntity)

    @Query("SELECT * FROM AdministrativeUnit")
    abstract fun selectAdministrativeUnitWithGeoLocations()
        : Flow<List<AdministrativeUnitWithCartographicBoundariesAndGeoLocations>>

    @Query(
        "SELECT * " +
        "FROM AdministrativeUnit " +
        "WHERE AdministrativeUnit.locality IS :locality AND " +
              "AdministrativeUnit.adminArea = :adminArea"
    )
    abstract suspend fun selectAdministrativeUnitsBy(
        locality: String, adminArea: String
    ): List<AdministrativeUnitEntity>
}