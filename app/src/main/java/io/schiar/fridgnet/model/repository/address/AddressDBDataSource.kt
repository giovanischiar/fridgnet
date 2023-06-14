package io.schiar.fridgnet.model.repository.address

import io.schiar.fridgnet.model.Address
import io.schiar.fridgnet.model.Coordinate
import io.schiar.fridgnet.model.datasource.room.AddressDAO
import io.schiar.fridgnet.model.datasource.room.relationentity.AddressWithCoordinates
import io.schiar.fridgnet.model.repository.location.toCoordinate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddressDBDataSource(private val addressDAO: AddressDAO): AddressDataSource {
    override fun fetchAddressBy(coordinate: Coordinate): Address? {
        val (latitude, longitude) = coordinate
        return selectAddressBy(latitude = latitude, longitude = longitude)
    }

    suspend fun setup(onLoaded: (coordinate: Coordinate, address: Address) -> Unit) {
        coroutineScope {
            launch {
                withContext(Dispatchers.IO) { selectAddresses() }
                    .forEach { addressWithCoordinates ->
                    addressWithCoordinates.coordinates.forEach { coordinateEntity ->
                        val coordinate = coordinateEntity.toCoordinate()
                        val address = addressWithCoordinates.addressEntity.toAddress()
                        onLoaded(coordinate, address)
                    }
                }
            }
        }
    }

    fun insert(coordinate: Coordinate, address: Address) {
        val addressEntityID = insertOrUpdate(address = address)
        addressDAO.insert(
            coordinateEntity = coordinate.toCoordinateEntity(addressCoordinatesID = addressEntityID)
        )
    }

    private fun insertOrUpdate(address: Address): Long {
        return (addressDAO.selectAddressIDBy(name = address.name())
            ?: addressDAO.insert(addressEntity = address.toAddressEntity())
        )
    }

    private fun selectAddresses(): List<AddressWithCoordinates> {
        return addressDAO.selectAddressesWithCoordinates().map { addressWithCoordinates ->
            addressWithCoordinates
        }
    }

    private fun selectAddressBy(latitude: Double, longitude: Double): Address? {
        return addressDAO.selectAddressEntityBy(
            latitude = latitude, longitude = longitude
        )?.toAddress()
    }
}