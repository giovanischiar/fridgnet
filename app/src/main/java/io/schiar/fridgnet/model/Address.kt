package io.schiar.fridgnet.model

import io.schiar.fridgnet.model.AdministrativeUnit.CITY
import io.schiar.fridgnet.model.AdministrativeUnit.COUNTRY
import io.schiar.fridgnet.model.AdministrativeUnit.COUNTY
import io.schiar.fridgnet.model.AdministrativeUnit.STATE

data class Address(
    val id: Long = 0,
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

    fun name(administrativeUnit: AdministrativeUnit): String {
        return when(administrativeUnit) {
            CITY -> name()
            COUNTY -> {
                listOfNotNull(subAdminArea, adminArea, countryName).joinToString(separator = ", ")
            }
            STATE -> listOfNotNull(adminArea, countryName).joinToString(separator = ", ")
            COUNTRY -> countryName ?: ""
        }
    }
}