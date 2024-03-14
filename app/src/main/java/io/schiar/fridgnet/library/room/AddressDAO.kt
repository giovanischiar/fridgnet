package io.schiar.fridgnet.library.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import io.schiar.fridgnet.Log
import io.schiar.fridgnet.library.room.entity.AddressEntity
import io.schiar.fridgnet.library.room.entity.CoordinateEntity
import io.schiar.fridgnet.library.room.relationentity.AddressWithCoordinates
import io.schiar.fridgnet.model.Address
import io.schiar.fridgnet.model.Coordinate
import kotlinx.coroutines.flow.Flow

@Dao
abstract class AddressDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(addressEntity: AddressEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insert(coordinateEntity: CoordinateEntity): Long

    @Transaction
    open suspend fun insert(coordinate: Coordinate, address: Address) {
        val addressEntityID = insertOrUpdate(address = address) ?: return
        insert(
            coordinateEntity = coordinate.toCoordinateEntity(addressCoordinatesID = addressEntityID)
        )
    }

    @Query("SELECT * FROM Coordinate " +
           "WHERE addressCoordinatesID = (SELECT id FROM Address " +
              "WHERE locality = :locality AND " +
                    "subAdminArea = :subAdminArea AND " +
                    "adminArea = :adminArea AND " +
                    "countryName = :countryName LIMIT 1)")
    abstract fun selectCoordinates(
        locality: String?,
        subAdminArea: String?,
        adminArea: String?,
        countryName: String?
    ): Flow<List<CoordinateEntity>>

    @Query("SELECT * FROM Coordinate " +
            "WHERE addressCoordinatesID IN (SELECT id FROM Address " +
            "WHERE subAdminArea = :subAdminArea AND " +
                  "adminArea = :adminArea AND " +
                  "countryName = :countryName)")
    abstract fun selectCoordinates(
        subAdminArea: String?,
        adminArea: String?,
        countryName: String?
    ): Flow<List<CoordinateEntity>>

    @Query("SELECT * FROM Coordinate " +
            "WHERE addressCoordinatesID IN (SELECT id FROM Address " +
            "WHERE adminArea = :adminArea AND " +
            "countryName = :countryName)")
    abstract fun selectCoordinates(
        adminArea: String?,
        countryName: String?
    ): Flow<List<CoordinateEntity>>

    @Query("SELECT * FROM Coordinate " +
            "WHERE addressCoordinatesID IN (SELECT id FROM Address " +
            "WHERE countryName = :countryName)")
    abstract fun selectCoordinates(countryName: String?): Flow<List<CoordinateEntity>>

    private suspend fun insertOrUpdate(address: Address): Long? {
        val (locality, subAdminArea, adminArea) = address
        val storedAddressEntity = selectAddressBy(
            locality = locality ?: return null,
            adminArea = adminArea ?: return null
        )

        return if (storedAddressEntity != null) {
            if (storedAddressEntity.subAdminArea == null) {
                Log.d("Store Address", "Updating $locality county to $subAdminArea")
                update(storedAddressEntity.updateSubAdminArea(subAdminArea))
                storedAddressEntity.id
            }

            if (storedAddressEntity.subAdminArea != subAdminArea) {
                if (subAdminArea == null) {
                    Log.d(
                        "Store Address",
                        "Setting $locality to ${storedAddressEntity.subAdminArea}"
                    )
                    storedAddressEntity.id
                } else {
                    Log.d(
                        "Store Address",
                        "$locality is in ${storedAddressEntity.subAdminArea} or $subAdminArea?"
                    )
                    insert(addressEntity = address.toAddressEntity())
                }
            } else {
                storedAddressEntity.id
            }
        } else {
            insert(addressEntity = address.toAddressEntity())
        }
    }

    @Update
    abstract suspend fun update(addressEntity: AddressEntity)

    @Query("SELECT * FROM Address")
    abstract fun selectAddressesWithCoordinates(): Flow<List<AddressWithCoordinates>>

    @Query(
        "SELECT * FROM Address JOIN Coordinate ON Address.id is Coordinate.addressCoordinatesID " +
                "WHERE Coordinate.latitude == :latitude AND Coordinate.longitude == :longitude LIMIT 1"
    )
    abstract suspend fun selectAddressEntityBy(latitude: Double, longitude: Double): AddressWithCoordinates?

    @Query(
        "SELECT * FROM Address WHERE " +
                "Address.locality is :locality AND Address.adminArea = :adminArea"
    )
    abstract suspend fun selectAddressBy(locality: String, adminArea: String): AddressEntity?
}