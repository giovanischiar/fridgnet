package io.schiar.fridgnet.model.datasource

import io.schiar.fridgnet.model.Address
import io.schiar.fridgnet.model.AddressLocationsGeoLocations
import io.schiar.fridgnet.model.AdministrativeLevel
import io.schiar.fridgnet.model.GeoLocation
import kotlinx.coroutines.flow.Flow

interface AddressDataSource {
    suspend fun create(geoLocation: GeoLocation, address: Address)
    suspend fun retrieveAddressFor(geoLocation: GeoLocation)
    fun retrieve(): Flow<List<AddressLocationsGeoLocations>>
    fun retrieveGeoLocations(
        address: Address, administrativeLevel: AdministrativeLevel
    ): Flow<List<GeoLocation>>
}