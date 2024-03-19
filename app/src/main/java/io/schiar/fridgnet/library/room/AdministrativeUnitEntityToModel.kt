package io.schiar.fridgnet.library.room

import io.schiar.fridgnet.library.room.entity.AdministrativeUnitEntity
import io.schiar.fridgnet.model.AdministrativeUnit

fun AdministrativeUnitEntity.toAdministrativeUnit(): AdministrativeUnit {
    return AdministrativeUnit(
        id = id,
        locality = locality,
        subAdminArea = subAdminArea,
        adminArea = adminArea,
        countryName = countryName
    )
}

fun AdministrativeUnitEntity.updateSubAdminArea(subAdminArea: String?): AdministrativeUnitEntity {
    return AdministrativeUnitEntity(
        id = id,
        locality = locality,
        subAdminArea = this.subAdminArea ?: subAdminArea,
        adminArea = adminArea,
        countryName = countryName
    )
}