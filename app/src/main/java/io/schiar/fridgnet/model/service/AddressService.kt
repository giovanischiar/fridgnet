package io.schiar.fridgnet.model.service

import io.schiar.fridgnet.model.Address
import io.schiar.fridgnet.model.AddressLocationsCoordinates
import io.schiar.fridgnet.model.AdministrativeUnit
import io.schiar.fridgnet.model.Coordinate
import kotlinx.coroutines.flow.Flow

interface AddressService {
    suspend fun create(coordinate: Coordinate, address: Address)
    fun retrieve(): Flow<List<AddressLocationsCoordinates>>
    fun retrieveCoordinates(
        address: Address, administrativeUnit: AdministrativeUnit
    ): Flow<List<Coordinate>>
    suspend fun retrieve(coordinate: Coordinate): AddressLocationsCoordinates?
}