package io.schiar.fridgnet.model

enum class AdministrativeLevel {
    CITY,
    COUNTY,
    STATE,
    COUNTRY;

    fun with(administrativeUnitName: AdministrativeUnitName): String {
        return "$this|${administrativeUnitName.toString(administrativeLevel = this)}"
    }

    fun zIndex(): Float {
        return when (this) {
            CITY -> 1.3f
            COUNTY -> 1.2f
            STATE -> 1.1f
            COUNTRY -> 1.0f
        }
    }
}