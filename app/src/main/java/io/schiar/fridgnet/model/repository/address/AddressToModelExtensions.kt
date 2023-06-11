package io.schiar.fridgnet.model.repository.address

import io.schiar.fridgnet.model.Address
import android.location.Address as AndroidAddress

fun AndroidAddress.toModelAddress(): Address {
    return Address(
        locality = if (countryName != "Brazil") locality else subAdminArea,
        subAdminArea = subAdminArea,
        adminArea = adminArea,
        countryName = countryName
    )
}