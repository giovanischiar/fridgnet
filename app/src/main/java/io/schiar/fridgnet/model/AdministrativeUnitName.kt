package io.schiar.fridgnet.model

import io.schiar.fridgnet.model.AdministrativeLevel.CITY
import io.schiar.fridgnet.model.AdministrativeLevel.COUNTRY
import io.schiar.fridgnet.model.AdministrativeLevel.COUNTY
import io.schiar.fridgnet.model.AdministrativeLevel.STATE

data class AdministrativeUnitName(
    val id: Long = 0,
    val locality: String?,
    val subAdminArea: String?,
    val adminArea: String?,
    val countryName: String?
) {
    override fun toString(): String {
        return listOfNotNull(
            locality, subAdminArea, adminArea, countryName
        ).joinToString(separator = ", ")
    }

    fun toString(administrativeLevel: AdministrativeLevel): String {
        return when(administrativeLevel) {
            CITY -> toString()
            COUNTY -> {
                listOfNotNull(subAdminArea, adminArea, countryName).joinToString(separator = ", ")
            }
            STATE -> listOfNotNull(adminArea, countryName).joinToString(separator = ", ")
            COUNTRY -> countryName ?: ""
        }
    }
}