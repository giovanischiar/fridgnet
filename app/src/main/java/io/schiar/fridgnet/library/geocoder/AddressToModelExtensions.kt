package io.schiar.fridgnet.library.geocoder

import android.location.Address
import io.schiar.fridgnet.model.AdministrativeUnitName

/**
 * Converts an [Address] object, which represents a physical location, into an
 * [AdministrativeUnitName]
 * object. This [AdministrativeUnitName] object provides a structured breakdown of the
 * administrative units associated with the address (e.g., city, state, country).
 *
 * **Note:** Special handling is applied for addresses from Brazil. Since Brazilian addresses often
 * lack a dedicated `locality` field (typically representing the city), this function uses the
 * `subAdminArea` field (which might hold city information) for the `locality` in the resulting
 * `AdministrativeUnitName` object.
 *
 * @return The corresponding [AdministrativeUnitName] object representing the administrative units
 * associated with the address.
 */
fun Address.toAdministrativeUnitName(): AdministrativeUnitName {
    return AdministrativeUnitName(
        locality = if (countryName != "Brazil") locality else subAdminArea,
        subAdminArea = subAdminArea,
        adminArea = adminArea,
        countryName = countryName
    )
}