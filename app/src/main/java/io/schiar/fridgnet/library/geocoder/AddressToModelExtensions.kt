package io.schiar.fridgnet.library.geocoder

import android.location.Address
import io.schiar.fridgnet.model.AdministrativeUnitName

fun Address.toAdministrativeUnitName(): AdministrativeUnitName {
    return AdministrativeUnitName(
        locality = if (countryName != "Brazil") locality else subAdminArea,
        subAdminArea = subAdminArea,
        adminArea = adminArea,
        countryName = countryName
    )
}