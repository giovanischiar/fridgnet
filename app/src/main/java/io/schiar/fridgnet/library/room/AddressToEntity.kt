package io.schiar.fridgnet.library.room

import io.schiar.fridgnet.library.room.entity.AddressEntity
import io.schiar.fridgnet.model.Address

fun Address.toAddressEntity(): AddressEntity {
    return AddressEntity(
        locality = locality,
        subAdminArea = subAdminArea,
        adminArea = adminArea,
        countryName = countryName,
        administrativeUnit = administrativeUnit.toString()
    )
}