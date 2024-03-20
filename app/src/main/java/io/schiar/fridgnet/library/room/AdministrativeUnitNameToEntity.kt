package io.schiar.fridgnet.library.room

import io.schiar.fridgnet.library.room.entity.AdministrativeUnitNameEntity
import io.schiar.fridgnet.model.AdministrativeUnitName

fun AdministrativeUnitName.toAdministrativeUnitNameEntity(): AdministrativeUnitNameEntity {
    return AdministrativeUnitNameEntity(
        id = id,
        locality = locality,
        subAdminArea = subAdminArea,
        adminArea = adminArea,
        countryName = countryName
    )
}

fun AdministrativeUnitName.toAdministrativeUnitNameEntity(id: Long): AdministrativeUnitNameEntity {
    return AdministrativeUnitNameEntity(
        id = id,
        locality = locality,
        subAdminArea = subAdminArea,
        adminArea = adminArea,
        countryName = countryName
    )
}