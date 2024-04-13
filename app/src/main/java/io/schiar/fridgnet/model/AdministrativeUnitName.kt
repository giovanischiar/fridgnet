package io.schiar.fridgnet.model

import io.schiar.fridgnet.model.AdministrativeLevel.CITY
import io.schiar.fridgnet.model.AdministrativeLevel.COUNTRY
import io.schiar.fridgnet.model.AdministrativeLevel.COUNTY
import io.schiar.fridgnet.model.AdministrativeLevel.STATE

/**
 * Represents the name of an administrative unit, providing flexibility for different regional
 * structures.
 *
 * @property id           The database ID of the administrative unit name (defaults to 0).
 * @property locality     The name of the first administrative level (e.g., city). Can be null
 * (optional).
 * @property subAdminArea The name of the second administrative level for some countries
 * (e.g., county in USA). Can be null (optional).
 * @property adminArea    The name of a higher administrative level for some countries
 * (e.g., state in USA). Can be null (optional).
 * @property countryName  The name of the country. Can be null (optional).
 */
data class AdministrativeUnitName(
    val id: Long = 0,
    val locality: String?,
    val subAdminArea: String?,
    val adminArea: String?,
    val countryName: String?
) {
    /**
     * The stringified version of the administrative unit name.
     *
     * @return the full name of the administrative unit which each level separated by comma.
     */
    override fun toString(): String {
        return listOfNotNull(
            locality, subAdminArea, adminArea, countryName
        ).joinToString(separator = ", ")
    }

    /**
     * @param administrativeLevel which administration level the administrative unit will use.
     * @return                    the stringified version of the administrative unit name based of
     *                            administrative level
     */
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