package io.schiar.fridgnet.library.room

import io.schiar.fridgnet.library.room.entity.AdministrativeUnitNameEntity
import io.schiar.fridgnet.model.AdministrativeUnitName

fun AdministrativeUnitNameEntity.toAdministrativeUnitName(): AdministrativeUnitName {
    return AdministrativeUnitName(
        id = id,
        locality = locality,
        subAdminArea = subAdminArea,
        adminArea = adminArea,
        countryName = countryName
    )
}

fun AdministrativeUnitNameEntity.updateSubAdminArea(subAdminArea: String?): AdministrativeUnitNameEntity {
    return AdministrativeUnitNameEntity(
        id = id,
        locality = locality,
        subAdminArea = this.subAdminArea ?: subAdminArea,
        adminArea = adminArea,
        countryName = countryName
    )
}