package io.schiar.fridgnet.library.room

import io.schiar.fridgnet.library.room.entity.AdministrativeUnitNameEntity
import io.schiar.fridgnet.model.AdministrativeUnitName

/**
 * Converts the [AdministrativeUnitName] into its corresponding entity
 * [AdministrativeUnitNameEntity] using the existing ID from the AdministrativeUnitName object.
 * @return a new AdministrativeUnitNameEntity object
 */
fun AdministrativeUnitName.toAdministrativeUnitNameEntity(): AdministrativeUnitNameEntity {
    return AdministrativeUnitNameEntity(
        id = id,
        locality = locality,
        subAdminArea = subAdminArea,
        adminArea = adminArea,
        countryName = countryName
    )
}

/**
 * Converts the [AdministrativeUnitName] into its corresponding entity
 * [AdministrativeUnitNameEntity] with a specified ID.
 *
 * @param id the specific ID to assign to the new AdministrativeUnitNameEntity
 * @return the [AdministrativeUnitNameEntity] converted
 */
fun AdministrativeUnitName.toAdministrativeUnitNameEntity(id: Long): AdministrativeUnitNameEntity {
    return AdministrativeUnitNameEntity(
        id = id,
        locality = locality,
        subAdminArea = subAdminArea,
        adminArea = adminArea,
        countryName = countryName
    )
}