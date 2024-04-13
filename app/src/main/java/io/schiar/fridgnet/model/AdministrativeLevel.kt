package io.schiar.fridgnet.model

/**
 * Represents the administrative level of an AdministrativeUnit, using a simplified format common in
 * countries like the USA.
 *
 * **Note:** This simplification might not be suitable for all countries with different
 * administrative structures.
 */
enum class AdministrativeLevel {
    /** Represents a city or a town. */
    CITY,
    /** Represents a county or a similar sub-administrative area. */
    COUNTY,
    /** Represents a state or a province. */
    STATE,
    /** Represents a country. */
    COUNTRY;

    /**
     * The name of an administrative unit considering its administrativeLevel. This method is used
     * to disambiguate some administration units that are the same name.
     *
     * @param administrativeUnitName the administrative unit name used.
     * @return                       the administrative level name concatenated with the
     *                               administrative unit name.
     */
    fun with(administrativeUnitName: AdministrativeUnitName): String {
        return "$this|${administrativeUnitName.toString(administrativeLevel = this)}"
    }

    /**
     * @return the z index used when drawing on the map, the bigger the level, bigger the z index.
     */
    val zIndex: Float get() {
        return when (this) { CITY -> 1.3f; COUNTY -> 1.2f; STATE -> 1.1f; COUNTRY -> 1.0f }
    }

    /**
     * @return used by view to determine the number of column when showing in the screen, the bigger
     *         the level the fewer elements per column.
     */
    val administrativeUnitSize: Int get() {
        return when (this) { CITY -> 4; COUNTY -> 3; STATE -> 3; COUNTRY -> 1 }
    }
}