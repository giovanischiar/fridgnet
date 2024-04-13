package io.schiar.fridgnet.library.room

import io.schiar.fridgnet.library.room.entity.AdministrativeUnitNameEntity
import io.schiar.fridgnet.model.AdministrativeUnitName

/**
 * Use all the information within a [AdministrativeUnitNameEntity] to build a
 * [AdministrativeUnitName] object
 *
 * @return the [AdministrativeUnitName] converted
 */
fun AdministrativeUnitNameEntity.toAdministrativeUnitName(): AdministrativeUnitName {
    return AdministrativeUnitName(
        id = id,
        locality = locality,
        subAdminArea = subAdminArea,
        adminArea = adminArea,
        countryName = countryName
    )
}

/**
 * Updates the subAdminArea property of the current AdministrativeUnitNameEntity to use a new value.
 *
 * If the provided `subAdminArea` is null, the existing subAdminArea value is preserved.
 *
 * @param subAdminArea the new sub admin area name to update (can be null).
 * @return the updated AdministrativeUnitNameEntity object.
 */
fun AdministrativeUnitNameEntity.updateSubAdminArea(subAdminArea: String?)
    : AdministrativeUnitNameEntity {
    return AdministrativeUnitNameEntity(
        id = id,
        locality = locality,
        subAdminArea = this.subAdminArea ?: subAdminArea,
        adminArea = adminArea,
        countryName = countryName
    )
}