package io.schiar.fridgnet.model.datasource

import io.schiar.fridgnet.model.Address
import io.schiar.fridgnet.model.AddressLocationsCoordinates
import io.schiar.fridgnet.model.AdministrativeUnit
import io.schiar.fridgnet.model.Coordinate
import kotlinx.coroutines.flow.Flow

interface AddressDataSource {
    suspend fun create(coordinate: Coordinate, address: Address)
    suspend fun retrieveAddressFor(coordinate: Coordinate)
    fun retrieve(): Flow<List<AddressLocationsCoordinates>>
    fun retrieveCoordinates(
        address: Address, administrativeUnit: AdministrativeUnit
    ): Flow<List<Coordinate>>
}