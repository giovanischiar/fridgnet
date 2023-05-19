package io.schiar.fridgnet.model

data class Address(
    val locality: String?,
    val subAdminArea: String?,
    val adminArea: String?,
    val countryName: String?
) {
    fun name(): String {
        return if (locality != null) {
            "$locality, $subAdminArea, $adminArea, $countryName"
        } else if (subAdminArea != null) {
            "$subAdminArea, $adminArea, $countryName"
        } else if (this.adminArea != null) {
            "$adminArea, $countryName"
        } else this.countryName ?: "null"
    }
}

