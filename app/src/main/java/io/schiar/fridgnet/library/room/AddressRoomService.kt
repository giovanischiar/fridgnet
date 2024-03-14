package io.schiar.fridgnet.library.room

import io.schiar.fridgnet.model.Address
import io.schiar.fridgnet.model.AddressCoordinates
import io.schiar.fridgnet.model.AdministrativeUnit.CITY
import io.schiar.fridgnet.model.AdministrativeUnit.COUNTRY
import io.schiar.fridgnet.model.AdministrativeUnit.COUNTY
import io.schiar.fridgnet.model.AdministrativeUnit.STATE
import io.schiar.fridgnet.model.Coordinate
import io.schiar.fridgnet.model.service.AddressService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AddressRoomService(private val addressDAO: AddressDAO) : AddressService {
    override fun retrieveCoordinates(address: Address): Flow<List<Coordinate>> {
        val (locality, subAdminArea, adminArea, countryName, administrativeUnit) = address
        return when(administrativeUnit) {
            CITY -> addressDAO.selectCoordinates(
                locality = locality,
                subAdminArea = subAdminArea,
                adminArea = adminArea,
                countryName = countryName
            )
            COUNTY -> addressDAO.selectCoordinates(
                subAdminArea = subAdminArea,
                adminArea = adminArea,
                countryName = countryName
            )
            STATE -> addressDAO.selectCoordinates(adminArea = adminArea, countryName = countryName)
            COUNTRY -> addressDAO.selectCoordinates(countryName = countryName)
        }.map { it.toCoordinates() }
    }

    override suspend fun create(coordinate: Coordinate, address: Address) {
        addressDAO.insert(coordinate = coordinate, address = address)
    }

    override fun retrieve(): Flow<List<AddressCoordinates>> {
        return addressDAO.selectAddressesWithCoordinates().map { it.toAddressesCoordinates() }
    }

    override suspend fun retrieve(coordinate: Coordinate): AddressCoordinates? {
        val (_, latitude, longitude) = coordinate
        return addressDAO.selectAddressEntityBy(
            latitude = latitude, longitude = longitude
        )?.toAddressCoordinates()
    }
}