package io.schiar.fridgnet.model

enum class AdministrativeLevel {
    CITY,
    COUNTY,
    STATE,
    COUNTRY;

    fun with(administrativeUnitName: AdministrativeUnitName): String {
        return "$this|${administrativeUnitName.toString(administrativeLevel = this)}"
    }

    val zIndex: Float get() {
        return when (this) { CITY -> 1.3f; COUNTY -> 1.2f; STATE -> 1.1f; COUNTRY -> 1.0f }
    }

    val administrativeUnitSize: Int get() {
        return when (this) { CITY -> 4; COUNTY -> 3; STATE -> 3; COUNTRY -> 1 }
    }
}