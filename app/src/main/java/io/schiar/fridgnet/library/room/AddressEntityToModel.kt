package io.schiar.fridgnet.library.room

import io.schiar.fridgnet.library.room.entity.AddressEntity
import io.schiar.fridgnet.model.Address

fun AddressEntity.toAddress(): Address {
    return Address(
        id = id,
        locality = locality,
        subAdminArea = subAdminArea,
        adminArea = adminArea,
        countryName = countryName
    )
}

fun AddressEntity.updateSubAdminArea(subAdminArea: String?): AddressEntity {
    return AddressEntity(
        id = id,
        locality = locality,
        subAdminArea = this.subAdminArea ?: subAdminArea,
        adminArea = adminArea,
        countryName = countryName
    )
}