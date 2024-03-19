package io.schiar.fridgnet.library.geocoder

import android.location.Address
import io.schiar.fridgnet.model.AdministrativeUnit

fun Address.toAdministrativeUnit(): AdministrativeUnit {
    return AdministrativeUnit(
        locality = if (countryName != "Brazil") locality else subAdminArea,
        subAdminArea = subAdminArea,
        adminArea = adminArea,
        countryName = countryName
    )
}