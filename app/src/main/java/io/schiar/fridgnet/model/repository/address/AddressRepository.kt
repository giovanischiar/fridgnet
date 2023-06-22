package io.schiar.fridgnet.model.repository.address

import io.schiar.fridgnet.model.Address
import io.schiar.fridgnet.model.AdministrativeUnit
import io.schiar.fridgnet.model.Coordinate

interface AddressRepository {
    var currentAdministrativeUnit: AdministrativeUnit
    suspend fun setup()
    suspend fun fetchAddressBy(coordinate: Coordinate): Address?
    fun addressCoordinateFromAdministrativeUnit(
        administrativeUnit: AdministrativeUnit
    ): Map<Address, Set<Coordinate>>
    fun coordinatesFromAddressName(
        addressName: String, onNewCoordinateWasAdded: suspend () -> Unit
    ): Pair<Address?, Set<Coordinate>>
    fun subscribeForNewAddressAdded(callback: suspend (address: Address) -> Unit)
    fun currentAddressCoordinates(): Map<Address, Set<Coordinate>>
    fun currentCoordinates(): Pair<Address?, Set<Coordinate>>
}