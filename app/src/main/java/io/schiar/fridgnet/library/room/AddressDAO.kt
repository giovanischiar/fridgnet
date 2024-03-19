package io.schiar.fridgnet.library.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import io.schiar.fridgnet.Log
import io.schiar.fridgnet.library.room.entity.AddressEntity
import io.schiar.fridgnet.library.room.entity.GeoLocationEntity
import io.schiar.fridgnet.library.room.relationentity.AddressWithLocationsAndGeoLocations
import io.schiar.fridgnet.model.Address
import io.schiar.fridgnet.model.GeoLocation
import kotlinx.coroutines.flow.Flow

@Dao
abstract class AddressDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(addressEntity: AddressEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(geoLocationEntity: GeoLocationEntity): Long

    @Transaction
    open suspend fun insert(geoLocation: GeoLocation, address: Address) {
        val addressEntityID = insertOrUpdate(address = address) ?: return
        insert(
            geoLocationEntity = geoLocation.toGeoLocationEntity(
                addressGeoLocationsID = addressEntityID
            )
        )
    }

    @Query("SELECT * FROM GeoLocation " +
            "WHERE addressGeoLocationsID = (SELECT id FROM Address " +
              "WHERE locality = :locality AND " +
                    "subAdminArea = :subAdminArea AND " +
                    "adminArea = :adminArea AND " +
                    "countryName = :countryName LIMIT 1)")
    abstract fun selectGeoLocations(
        locality: String?,
        subAdminArea: String?,
        adminArea: String?,
        countryName: String?
    ): Flow<List<GeoLocationEntity>>

    @Query("SELECT * FROM GeoLocation " +
            "WHERE addressGeoLocationsID IN (SELECT id FROM Address " +
            "WHERE subAdminArea = :subAdminArea AND " +
                  "adminArea = :adminArea AND " +
                  "countryName = :countryName)")
    abstract fun selectGeoLocations(
        subAdminArea: String?,
        adminArea: String?,
        countryName: String?
    ): Flow<List<GeoLocationEntity>>

    @Query("SELECT * FROM GeoLocation " +
            "WHERE addressGeoLocationsID IN (SELECT id FROM Address " +
            "WHERE adminArea = :adminArea AND " +
            "countryName = :countryName)")
    abstract fun selectGeoLocations(
        adminArea: String?,
        countryName: String?
    ): Flow<List<GeoLocationEntity>>

    @Query("SELECT * FROM GeoLocation " +
            "WHERE addressGeoLocationsID IN (SELECT id FROM Address " +
            "WHERE countryName = :countryName)")
    abstract fun selectGeoLocations(countryName: String?): Flow<List<GeoLocationEntity>>

    private suspend fun insertOrUpdate(address: Address): Long? {
        val (_, locality, subAdminArea, adminArea) = address

        val storedAddressEntities = selectAddressesBy(
            locality = locality ?: return null,
            adminArea = adminArea ?: return null
        )

        if (storedAddressEntities.isEmpty()) {
            return insert(addressEntity = address.toAddressEntity())
        }

        if (storedAddressEntities.size > 1) {
            val multipleCounties = storedAddressEntities.map {
                it.subAdminArea
            }.joinToString(", ")
            val addressID = storedAddressEntities.filter { addressEntity ->
                addressEntity.subAdminArea == subAdminArea
            }.getOrNull(index = 0)?.id ?: 0
            return if (addressID != 0L) {
                addressID
            } else {
                Log.d(
                    "Store Address",
                    "Can't assume if $locality is in one of these counties: $multipleCounties, or $subAdminArea. Create a new Address"
                )
                insert(addressEntity = address.toAddressEntity(id = addressID))
            }
        }

        val storedAddressEntity = storedAddressEntities[0]

        if (storedAddressEntity.subAdminArea == null && subAdminArea != null) {
            Log.d(
                "Store Address",
                "Assuming ${storedAddressEntity.locality} is in $subAdminArea. Updating stored address"
            )
            update(storedAddressEntity.updateSubAdminArea(subAdminArea))
            return storedAddressEntity.id
        }

        if (subAdminArea == null && storedAddressEntity.subAdminArea != null) {
            Log.d(
                "Store Address",
                "Assuming ${address.locality} is in ${storedAddressEntity.subAdminArea}"
            )
            return storedAddressEntity.id
        }

        if (subAdminArea != storedAddressEntity.subAdminArea) {
            Log.d(
                "Store Address",
                "Can't assume if $locality is in ${storedAddressEntity.subAdminArea} or $subAdminArea. Inserting a new Address"
            )
            return insert(addressEntity = address.toAddressEntity())
        }

        return if (storedAddressEntity.id == 0L) null else storedAddressEntity.id
    }

    @Update
    abstract suspend fun update(addressEntity: AddressEntity)

    @Query("SELECT * FROM Address")
    abstract fun selectAddressesWithGeoLocations(): Flow<List<AddressWithLocationsAndGeoLocations>>

    @Query(
        "SELECT * FROM Address JOIN GeoLocation ON Address.id is GeoLocation.addressGeoLocationsID " +
                "WHERE GeoLocation.latitude == :latitude AND GeoLocation.longitude == :longitude LIMIT 1"
    )
    abstract suspend fun selectAddressEntityBy(latitude: Double, longitude: Double): AddressWithLocationsAndGeoLocations?

    @Query(
        "SELECT * FROM Address WHERE " +
                "Address.locality is :locality AND Address.adminArea = :adminArea"
    )
    abstract suspend fun selectAddressesBy(locality: String, adminArea: String): List<AddressEntity>
}