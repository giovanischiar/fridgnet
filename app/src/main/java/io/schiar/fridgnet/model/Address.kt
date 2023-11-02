package io.schiar.fridgnet.model

import io.schiar.fridgnet.model.AdministrativeUnit.CITY
import io.schiar.fridgnet.model.AdministrativeUnit.COUNTRY
import io.schiar.fridgnet.model.AdministrativeUnit.COUNTY
import io.schiar.fridgnet.model.AdministrativeUnit.STATE

data class Address(
    val locality: String?,
    val subAdminArea: String?,
    val adminArea: String?,
    val countryName: String?,
    val administrativeUnit: AdministrativeUnit = CITY
) {
    fun name(): String {
        return listOfNotNull(
            locality, subAdminArea, adminArea, countryName
        ).joinToString(separator = ", ")
    }

    fun allAddresses(): List<Address> {
        return listOf(
            this,
            this.addressAccordingTo(administrativeUnit = COUNTY),
            this.addressAccordingTo(administrativeUnit = STATE),
            this.addressAccordingTo(administrativeUnit = COUNTRY)
        )
    }

    fun addressAccordingTo(administrativeUnit: AdministrativeUnit): Address {
        return when (administrativeUnit) {
            CITY -> this

            COUNTY -> Address(
                locality = null,
                subAdminArea = subAdminArea,
                adminArea = adminArea,
                countryName = countryName,
                administrativeUnit = administrativeUnit
            )

            STATE -> Address(
                locality = null,
                subAdminArea = null,
                adminArea = adminArea,
                countryName = countryName,
                administrativeUnit = administrativeUnit
            )

            COUNTRY -> Address(
                locality = null,
                subAdminArea = null,
                adminArea = null,
                countryName = countryName,
                administrativeUnit = administrativeUnit
            )
        }
    }
}