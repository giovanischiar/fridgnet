package io.schiar.fridgnet.model.repository.address

import io.schiar.fridgnet.Log
import io.schiar.fridgnet.model.Address
import io.schiar.fridgnet.model.Coordinate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Collections.synchronizedMap as syncMapOf

class AddressGeocoderDBRepository(
    private val addressGeocoderDataSource: AddressDataSource,
    private val addressDBDataSource: AddressDBDataSource
): AddressRepository {
    private var coordinateAddress: MutableMap<Coordinate, Address> = syncMapOf(mutableMapOf())

    override suspend fun setup() {
        addressDBDataSource.setup(onLoaded = ::onLoaded)
    }

    private fun onLoaded(coordinate: Coordinate, address: Address) {
        coordinateAddress[coordinate] = address
    }

    override suspend fun fetchAddressBy(coordinate: Coordinate): Address? {
        log(coordinate = coordinate, "Let's check on the memory")
        return if (coordinateAddress.containsKey(coordinate)) {
            log(coordinate = coordinate, "It's already on the memory! Returning...")
            coordinateAddress[coordinate]
        } else {
            log(coordinate = coordinate, "Shoot! Time to search in the database")
            val addressFromDatabase = withContext(Dispatchers.IO) {
                addressDBDataSource.fetchAddressBy(coordinate = coordinate)
            }
            if (addressFromDatabase != null) {
                log(coordinate = coordinate, "it's on the database! Returning...")
                onLoaded(coordinate = coordinate, address = addressFromDatabase)
                addressFromDatabase
            } else {
                log(coordinate = coordinate, "Shoot! Time to search in the Geocoder")
                val addressFromGeocoder = withContext(Dispatchers.IO) {
                    addressGeocoderDataSource.fetchAddressBy(coordinate = coordinate)
                }
                if (addressFromGeocoder != null) {
                    log(coordinate = coordinate, "It's on the Geocoder! Returning...")
                    onLoaded(coordinate = coordinate, address = addressFromGeocoder)
                    coroutineScope {
                        launch(Dispatchers.IO) {
                            addressDBDataSource.insert(
                                coordinate = coordinate,
                                address = addressFromGeocoder
                            )
                        }
                    }
                }
                addressFromGeocoder
            }
        }
    }

    private fun log(coordinate: Coordinate, msg: String) {
        val (latitude, longitude) = coordinate
        Log.d(
            tag = "Coordinate to Address Feature",
            msg = "Fetching Address for ($latitude, $longitude): $msg"
        )
    }
}