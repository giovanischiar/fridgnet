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

@Dao
abstract class AdministrativeUnitNameDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(administrativeUnitNameEntity: AdministrativeUnitNameEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(geoLocationEntity: GeoLocationEntity): Long

    @Transaction
    open suspend fun insert(geoLocation: GeoLocation, administrativeUnitName: AdministrativeUnitName) {
        val administrativeUnitNameEntityID = insertOrUpdate(
            administrativeUnitName = administrativeUnitName
        ) ?: return
        insert(
            geoLocationEntity = geoLocation.toGeoLocationEntity(
                administrativeUnitNameGeoLocationsID = administrativeUnitNameEntityID
            )
        )
    }

    @Query(
        "SELECT * FROM GeoLocation " +
        "WHERE administrativeUnitNameGeoLocationsID = (" +
                    "SELECT id " +
                    "FROM AdministrativeUnitName " +
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
        "WHERE administrativeUnitNameGeoLocationsID IN (" +
                "SELECT id " +
                "FROM AdministrativeUnitName " +
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
        "WHERE administrativeUnitNameGeoLocationsID IN (" +
                "SELECT id " +
                "FROM AdministrativeUnitName " +
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
        "WHERE administrativeUnitNameGeoLocationsID IN (" +
                "SELECT id " +
                "FROM AdministrativeUnitName " +
                "WHERE countryName = :countryName" +
              ")"
    )
    abstract fun selectGeoLocations(countryName: String?): Flow<List<GeoLocationEntity>>

    private suspend fun insertOrUpdate(administrativeUnitName: AdministrativeUnitName): Long? {
        val (_, locality, subAdminArea, adminArea) = administrativeUnitName

        val storedAdministrativeUnitNameEntities = selectAdministrativeUnitNamesBy(
            locality = locality ?: return null,
            adminArea = adminArea ?: return null
        )

        if (storedAdministrativeUnitNameEntities.isEmpty()) {
            return insert(
                administrativeUnitNameEntity = administrativeUnitName.toAdministrativeUnitNameEntity()
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
                    administrativeUnitNameEntity = administrativeUnitName.toAdministrativeUnitNameEntity(
                        id = administrativeUnitNameID
                    )
                )
            }
        }

        val storedAdministrativeUnitNameEntity = storedAdministrativeUnitNameEntities[0]

        if (storedAdministrativeUnitNameEntity.subAdminArea == null && subAdminArea != null) {
            Log.d(
                tag = "Store AdministrativeUnitName",
                msg = "Assuming ${storedAdministrativeUnitNameEntity.locality} is in $subAdminArea. " +
                      "Updating stored administrativeUnitName"
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
                administrativeUnitNameEntity = administrativeUnitName.toAdministrativeUnitNameEntity()
            )
        }

        return if (storedAdministrativeUnitNameEntity.id == 0L) {
            null
        } else storedAdministrativeUnitNameEntity.id
    }

    @Update
    abstract suspend fun update(administrativeUnitNameEntity: AdministrativeUnitNameEntity)

    @Query("SELECT * FROM AdministrativeUnitName")
    abstract fun selectAdministrativeUnitNameWithCartographicBoundaries()
        : Flow<List<AdministrativeUnitNameWithCartographicBoundaries>>

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