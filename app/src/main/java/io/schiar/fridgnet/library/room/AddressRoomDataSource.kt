package io.schiar.fridgnet.library.room

import io.schiar.fridgnet.Log
import io.schiar.fridgnet.library.room.relationentity.AddressWithCoordinates
import io.schiar.fridgnet.model.Address
import io.schiar.fridgnet.model.Coordinate
import io.schiar.fridgnet.model.datasource.AddressDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddressRoomDataSource(private val addressDAO: AddressDAO) : AddressDataSource {
    override fun fetchAddressBy(coordinate: Coordinate): Address? {
        val (latitude, longitude) = coordinate
        return selectAddressBy(latitude = latitude, longitude = longitude)
    }

    override suspend fun setup(onLoaded: suspend (coordinate: Coordinate, address: Address) -> Unit) {
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

    override fun insert(coordinate: Coordinate, address: Address) {
        val addressEntityID = insertOrUpdate(address = address) ?: return
        addressDAO.insert(
            coordinateEntity = coordinate.toCoordinateEntity(addressCoordinatesID = addressEntityID)
        )
    }

    private fun insertOrUpdate(address: Address): Long? {
        val (locality, subAdminArea, adminArea) = address
        val storedAddressEntity = addressDAO.selectAddressBy(
            locality = locality ?: return null,
            adminArea = adminArea ?: return null
        )

        return if (storedAddressEntity != null) {
            if (storedAddressEntity.subAdminArea == null) {
                Log.d("Store Address", "Updating $locality county to $subAdminArea")
                addressDAO.update(storedAddressEntity.updateSubAdminArea(subAdminArea))
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
                    addressDAO.insert(addressEntity = address.toAddressEntity())
                }
            } else {
                storedAddressEntity.id
            }
        } else {
            addressDAO.insert(addressEntity = address.toAddressEntity())
        }
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