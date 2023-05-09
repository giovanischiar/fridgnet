package io.schiar.fridgnet.model

data class Address(
    val locality: String?,
    val subAdminArea: String?,
    val adminArea: String?,
    val countryName: String?
)

fun Address.name(): String {
    return if (this.locality != null) {
        "${this.locality}, ${this.subAdminArea}, ${this.adminArea}, ${this.countryName}"
    } else if (this.subAdminArea != null) {
        "${this.subAdminArea}, ${this.adminArea}, ${this.countryName}"
    } else if (this.adminArea != null) {
        "${this.adminArea}, ${this.countryName}"
    } else this.countryName ?: "null"
}