package io.schiar.fridgnet.library.room

import io.schiar.fridgnet.library.room.entity.AdministrativeUnitEntity
import io.schiar.fridgnet.model.AdministrativeUnit

fun AdministrativeUnit.toAdministrativeUnitEntity(): AdministrativeUnitEntity {
    return AdministrativeUnitEntity(
        id = id,
        locality = locality,
        subAdminArea = subAdminArea,
        adminArea = adminArea,
        countryName = countryName
    )
}

fun AdministrativeUnit.toAdministrativeUnitEntity(id: Long): AdministrativeUnitEntity {
    return AdministrativeUnitEntity(
        id = id,
        locality = locality,
        subAdminArea = subAdminArea,
        adminArea = adminArea,
        countryName = countryName
    )
}