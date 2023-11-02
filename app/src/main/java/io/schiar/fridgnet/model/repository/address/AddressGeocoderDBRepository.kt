package io.schiar.fridgnet.model.repository.address

import io.schiar.fridgnet.Log
import io.schiar.fridgnet.model.Address
import io.schiar.fridgnet.model.AdministrativeUnit
import io.schiar.fridgnet.model.AdministrativeUnit.CITY
import io.schiar.fridgnet.model.AdministrativeUnit.COUNTRY
import io.schiar.fridgnet.model.AdministrativeUnit.COUNTY
import io.schiar.fridgnet.model.AdministrativeUnit.STATE
import io.schiar.fridgnet.model.Coordinate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Collections.synchronizedMap as syncMapOf

class AddressGeocoderDBRepository(
    private val addressGeocoderDataSource: AddressDataSource,
    private val addressDBDataSource: AddressDBDataSource
) : AddressRepository {
    private val coordinateAddress: MutableMap<Coordinate, Address> = syncMapOf(mutableMapOf())
    private val nameAddress: MutableMap<String, Address> = syncMapOf(mutableMapOf())
    private val cityCoordinates: MutableMap<Address, Set<Coordinate>> = syncMapOf(mutableMapOf())
    private val countyCoordinates: MutableMap<Address, Set<Coordinate>> = syncMapOf(mutableMapOf())
    private val stateCoordinates: MutableMap<Address, Set<Coordinate>> = syncMapOf(mutableMapOf())
    private val countryCoordinates: MutableMap<Address, Set<Coordinate>> = syncMapOf(mutableMapOf())

    override var currentAdministrativeUnit = CITY
    private var currentAddress: Address? = null
    private var onNewAddressAdded: suspend (address: Address) -> Unit = {}
    private var onNewCoordinateWasAdded: suspend () -> Unit = {}

    override suspend fun setup() {
        addressDBDataSource.setup(onLoaded = ::onLoaded)
    }

    override fun coordinatesFromAddressName(
        addressName: String, onNewCoordinateWasAdded: suspend () -> Unit
    ): Pair<Address?, Set<Coordinate>> {
        this.onNewCoordinateWasAdded = onNewCoordinateWasAdded
        val address = nameAddress[addressName] ?: return Pair(null, emptySet())
        currentAddress = address
        return Pair(
            address, addressCoordinateFromAdministrativeUnit(
                administrativeUnit = address.administrativeUnit
            )[address] ?: emptySet()
        )
    }

    override fun subscribeForNewAddressAdded(callback: suspend (address: Address) -> Unit) {
        onNewAddressAdded = callback
    }

    override fun currentAddressCoordinates(): Map<Address, Set<Coordinate>> {
        return addressCoordinateFromAdministrativeUnit(currentAdministrativeUnit)
    }

    override fun currentCoordinates(): Pair<Address?, Set<Coordinate>> {
        val coordinates =
            addressCoordinateFromAdministrativeUnit(currentAdministrativeUnit)[currentAddress]
                ?: emptySet()
        return Pair(currentAddress, coordinates)
    }

    override fun addressCoordinateFromAdministrativeUnit(
        administrativeUnit: AdministrativeUnit
    ): Map<Address, Set<Coordinate>> {
        return when (administrativeUnit) {
            CITY -> cityCoordinates
            COUNTY -> countyCoordinates
            STATE -> stateCoordinates
            COUNTRY -> countryCoordinates
        }
    }

    private suspend fun onLoaded(coordinate: Coordinate, address: Address) {
        coordinateAddress[coordinate] = address
        address.allAddresses().forEach { subAddress ->
            nameAddress[subAddress.name()] = address
            val coordinates =
                addCoordinateToEachAddress(coordinate = coordinate, address = subAddress)
            if (coordinates.size == 1 && address.administrativeUnit == currentAdministrativeUnit) {
                onNewAddressAdded(subAddress)
            }

            if (currentAddress == address) {
                onNewCoordinateWasAdded()
            }
        }
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

    private fun addCoordinateToEachAddress(
        address: Address,
        coordinate: Coordinate
    ): List<Coordinate> {
        return when (address.administrativeUnit) {
            CITY -> {
                val coordinates = cityCoordinates.getOrDefault(address, emptySet()) + coordinate
                cityCoordinates[address] = coordinates
                coordinates.toList()
            }

            COUNTY -> {
                val coordinates = countyCoordinates.getOrDefault(address, emptySet()) + coordinate
                countyCoordinates[address] = coordinates
                coordinates.toList()
            }

            STATE -> {
                val coordinates = stateCoordinates.getOrDefault(address, emptySet()) + coordinate
                stateCoordinates[address] = coordinates
                coordinates.toList()
            }

            COUNTRY -> {
                val coordinates = countryCoordinates.getOrDefault(address, emptySet()) + coordinate
                countryCoordinates[address] = coordinates
                coordinates.toList()
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