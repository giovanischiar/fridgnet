package io.schiar.fridgnet.library.room

import io.schiar.fridgnet.model.Address
import io.schiar.fridgnet.model.AddressLocationsGeoLocations
import io.schiar.fridgnet.model.AdministrativeLevel
import io.schiar.fridgnet.model.AdministrativeLevel.CITY
import io.schiar.fridgnet.model.AdministrativeLevel.COUNTRY
import io.schiar.fridgnet.model.AdministrativeLevel.COUNTY
import io.schiar.fridgnet.model.AdministrativeLevel.STATE
import io.schiar.fridgnet.model.GeoLocation
import io.schiar.fridgnet.model.service.AddressService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AddressRoomService(private val addressDAO: AddressDAO) : AddressService {
    override fun retrieveGeoLocations(
        address: Address, administrativeLevel: AdministrativeLevel
    ): Flow<List<GeoLocation>> {
        val (_, locality, subAdminArea, adminArea, countryName) = address
        return when(administrativeLevel) {
            CITY -> addressDAO.selectGeoLocations(
                locality = locality,
                subAdminArea = subAdminArea,
                adminArea = adminArea,
                countryName = countryName
            )
            COUNTY -> addressDAO.selectGeoLocations(
                subAdminArea = subAdminArea, adminArea = adminArea, countryName = countryName
            )
            STATE -> addressDAO.selectGeoLocations(adminArea = adminArea, countryName = countryName)
            COUNTRY -> addressDAO.selectGeoLocations(countryName = countryName)
        }.map { it.toGeoLocations() }
    }

    override suspend fun create(geoLocation: GeoLocation, address: Address) {
        addressDAO.insert(geoLocation = geoLocation, address = address)
    }

    override fun retrieve(): Flow<List<AddressLocationsGeoLocations>> {
        return addressDAO.selectAddressesWithGeoLocations().map {
            it.toAddressLocationsGeoLocations()
        }
    }
}