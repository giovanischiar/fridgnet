package io.schiar.fridgnet.library.room

import io.schiar.fridgnet.library.room.entity.AddressEntity
import io.schiar.fridgnet.model.Address

fun Address.toAddressEntity(): AddressEntity {
    return AddressEntity(
        id = id,
        locality = locality,
        subAdminArea = subAdminArea,
        adminArea = adminArea,
        countryName = countryName
    )
}

fun Address.toAddressEntity(id: Long): AddressEntity {
    return AddressEntity(
        id = id,
        locality = locality,
        subAdminArea = subAdminArea,
        adminArea = adminArea,
        countryName = countryName
    )
}