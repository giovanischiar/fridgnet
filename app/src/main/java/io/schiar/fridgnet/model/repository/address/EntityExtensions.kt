package io.schiar.fridgnet.model.repository.address

import io.schiar.fridgnet.model.Address
import io.schiar.fridgnet.model.AdministrativeUnit
import io.schiar.fridgnet.model.Coordinate
import io.schiar.fridgnet.model.datasource.room.entity.AddressEntity
import io.schiar.fridgnet.model.datasource.room.entity.CoordinateEntity

fun AddressEntity.toAddress(): Address {
    return Address(
        locality = locality,
        subAdminArea = subAdminArea,
        adminArea = adminArea,
        countryName = countryName,
        administrativeUnit = AdministrativeUnit.valueOf(administrativeUnit)
    )
}

fun Address.toAddressEntity(): AddressEntity {
    return AddressEntity(
        locality = locality,
        subAdminArea = subAdminArea,
        adminArea = adminArea,
        countryName = countryName,
        administrativeUnit = administrativeUnit.toString(),
        name = name()
    )
}

fun Coordinate.toCoordinateEntity(addressCoordinatesID: Long): CoordinateEntity {
    return CoordinateEntity(
        addressCoordinatesID = addressCoordinatesID,
        latitude = latitude,
        longitude = longitude
    )
}