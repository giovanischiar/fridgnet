package io.schiar.fridgnet.library.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import io.schiar.fridgnet.library.room.entity.AddressEntity
import io.schiar.fridgnet.library.room.entity.CoordinateEntity
import io.schiar.fridgnet.library.room.relationentity.AddressWithCoordinates
import kotlinx.coroutines.flow.Flow

@Dao
interface AddressDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(addressEntity: AddressEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(coordinateEntity: CoordinateEntity): Long

    @Update
    suspend fun update(addressEntity: AddressEntity)

    @Query("SELECT * FROM Address")
    fun selectAddressesWithCoordinates(): Flow<List<AddressWithCoordinates>>

    @Query(
        "SELECT * FROM Address JOIN Coordinate ON Address.id is Coordinate.addressCoordinatesID " +
                "WHERE Coordinate.latitude == :latitude AND Coordinate.longitude == :longitude LIMIT 1"
    )
    suspend fun selectAddressEntityBy(latitude: Double, longitude: Double): AddressEntity?

    @Query(
        "SELECT * FROM Address WHERE " +
                "Address.locality is :locality AND Address.adminArea = :adminArea"
    )
    suspend fun selectAddressBy(locality: String, adminArea: String): AddressEntity?
}