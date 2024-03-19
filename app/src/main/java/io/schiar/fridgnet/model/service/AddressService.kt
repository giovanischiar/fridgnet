package io.schiar.fridgnet.model.service

import io.schiar.fridgnet.model.Address
import io.schiar.fridgnet.model.AddressLocationsGeoLocations
import io.schiar.fridgnet.model.AdministrativeUnit
import io.schiar.fridgnet.model.GeoLocation
import kotlinx.coroutines.flow.Flow

interface AddressService {
    suspend fun create(geoLocation: GeoLocation, address: Address)
    fun retrieve(): Flow<List<AddressLocationsGeoLocations>>
    fun retrieveGeoLocations(
        address: Address, administrativeUnit: AdministrativeUnit
    ): Flow<List<GeoLocation>>
}