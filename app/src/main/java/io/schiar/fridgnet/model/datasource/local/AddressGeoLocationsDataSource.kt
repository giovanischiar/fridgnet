package io.schiar.fridgnet.model.datasource.local

import io.schiar.fridgnet.Log
import io.schiar.fridgnet.model.Address
import io.schiar.fridgnet.model.AddressLocationsGeoLocations
import io.schiar.fridgnet.model.AdministrativeLevel
import io.schiar.fridgnet.model.GeoLocation
import io.schiar.fridgnet.model.datasource.AddressDataSource
import io.schiar.fridgnet.model.datasource.retriever.AddressRetriever
import io.schiar.fridgnet.model.service.AddressService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import java.util.Collections.synchronizedSet as syncSetOf

class AddressGeoLocationsDataSource(
    private val addressRetriever: AddressRetriever,
    private val addressService: AddressService
): AddressDataSource {
    private val geoLocationSet = syncSetOf(mutableSetOf<GeoLocation>())

    override suspend fun create(geoLocation: GeoLocation, address: Address) {
        log(geoLocation, "creating address ${address.name()}")
        addressService.create(geoLocation = geoLocation, address = address)
    }

    private fun updateCacheFromService(
        addressesLocationsGeoLocations: List<AddressLocationsGeoLocations>
    ) {
        addressesLocationsGeoLocations.forEach { addressesLocationsGeoLocation ->
            geoLocationSet.addAll(elements = addressesLocationsGeoLocation.geoLocations)
        }
    }

    override suspend fun retrieveAddressFor(geoLocation: GeoLocation) {
        if (geoLocationSet.contains(element = geoLocation)) return
        geoLocationSet.add(element = geoLocation)
        log(geoLocation = geoLocation, "It's not on memory, retrieving using the Geocoder")
        val addressFromRetriever = addressRetriever.retrieve(geoLocation = geoLocation)
        if (addressFromRetriever != null) {
            create(geoLocation = geoLocation, address = addressFromRetriever)
            return
        }
        log(geoLocation = geoLocation, "It's not on the Geocoder!")
    }

    override fun retrieve(): Flow<List<AddressLocationsGeoLocations>> {
        return addressService.retrieve().onEach(::updateCacheFromService)
    }

    override fun retrieveGeoLocations(
        address: Address, administrativeLevel: AdministrativeLevel
    ): Flow<List<GeoLocation>> {
        return addressService.retrieveGeoLocations(
            address = address, administrativeLevel = administrativeLevel
        )
    }

    private fun log(geoLocation: GeoLocation, msg: String) {
        val (_, latitude, longitude) = geoLocation
        Log.d(
            tag = "GeoLocation to Address Feature",
            msg = "Retrieving Address for ($latitude, $longitude): $msg"
        )
    }
}