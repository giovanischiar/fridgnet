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
}