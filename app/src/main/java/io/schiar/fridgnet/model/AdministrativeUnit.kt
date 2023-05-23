package io.schiar.fridgnet.model

enum class AdministrativeUnit {
    CITY,
    COUNTY,
    STATE,
    COUNTRY;

    fun zIndex(): Float {
        return when (this) {
            CITY -> 1.3f
            COUNTY -> 1.2f
            STATE -> 1.1f
            COUNTRY -> 1.0f
        }
    }
}