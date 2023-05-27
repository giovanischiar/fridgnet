package io.schiar.fridgnet.model

data class Address(
    val locality: String?,
    val subAdminArea: String?,
    val adminArea: String?,
    val countryName: String?
) {
    fun name(): String {
        return listOfNotNull(
            locality, subAdminArea, adminArea, countryName
        ).joinToString(separator = ", ")
    }

    fun addressAccordingTo(administrativeUnit: AdministrativeUnit): Address {
        return when(administrativeUnit) {
            AdministrativeUnit.CITY -> this

            AdministrativeUnit.COUNTY -> Address(
                locality = null,
                subAdminArea = subAdminArea,
                adminArea = adminArea,
                countryName = countryName
            )

            AdministrativeUnit.STATE -> Address(
                locality = null,
                subAdminArea = null,
                adminArea = adminArea,
                countryName = countryName
            )

            AdministrativeUnit.COUNTRY -> Address(
                locality = null,
                subAdminArea = null,
                adminArea = null,
                countryName = countryName
            )
        }
    }
}