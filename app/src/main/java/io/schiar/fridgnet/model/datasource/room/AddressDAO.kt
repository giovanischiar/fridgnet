package io.schiar.fridgnet.model.datasource.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.schiar.fridgnet.model.datasource.room.entity.AddressEntity
import io.schiar.fridgnet.model.datasource.room.entity.CoordinateEntity
import io.schiar.fridgnet.model.datasource.room.relationentity.AddressWithCoordinates

@Dao
interface AddressDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(addressEntity: AddressEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(coordinateEntity: CoordinateEntity): Long

    @Query("SELECT * FROM Address")
    fun selectAddressesWithCoordinates(): List<AddressWithCoordinates>

    @Query(
        "SELECT * FROM Address JOIN Coordinate ON Address.id is Coordinate.addressCoordinatesID " +
        "WHERE Coordinate.latitude == :latitude AND Coordinate.longitude == :longitude LIMIT 1"
    )
    fun selectAddressEntityBy(latitude: Double, longitude: Double): AddressEntity?

    @Query("SELECT id FROM Address WHERE Address.name == :name")
    fun selectAddressIDBy(name: String): Long?
}