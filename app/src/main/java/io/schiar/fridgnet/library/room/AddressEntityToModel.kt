package io.schiar.fridgnet.library.room

import io.schiar.fridgnet.library.room.entity.AddressEntity
import io.schiar.fridgnet.model.Address
import io.schiar.fridgnet.model.AdministrativeUnit

fun AddressEntity.toAddress(): Address {
    return Address(
        locality = locality,
        subAdminArea = subAdminArea,
        adminArea = adminArea,
        countryName = countryName,
        administrativeUnit = AdministrativeUnit.valueOf(administrativeUnit)
    )
}

fun AddressEntity.updateSubAdminArea(subAdminArea: String?): AddressEntity {
    return AddressEntity(
        id = id,
        locality = locality,
        subAdminArea = this.subAdminArea ?: subAdminArea,
        adminArea = adminArea,
        countryName = countryName,
        administrativeUnit = administrativeUnit
    )
}