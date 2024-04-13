package io.schiar.fridgnet.model

import io.schiar.fridgnet.library.util.IdentitySet

/**
 * Represents an administrative unit with its properties and relationships.
 *
 * @property name                  The full name of the administrative unit, potentially including
 *                                 names of outer administrative units separated by commas
 *                                 (or other separators).
 * @property administrativeLevel   The [AdministrativeLevel] of this unit (e.g., state, county,
 *                                 city).
 * @property cartographicBoundary  The CartographicBoundary object representing the outline of the
 *                                 unit on a map (can be null).
 * @property subAdministrativeUnits A set containing all child administrative units under this unit.
 * @property images                A set containing all [Image] objects associated with this
 *                                 administrative unit (presumably images taken within its
 *                                 boundaries).
 */
data class AdministrativeUnit(
    val name: String,
    val administrativeLevel: AdministrativeLevel,
    var cartographicBoundary: CartographicBoundary? = null,
    val subAdministrativeUnits: IdentitySet<AdministrativeUnit> = IdentitySet(),
    val images: MutableSet<Image> = mutableSetOf()
) {
    /**
     * Returns the first part of the unit's name, excluding names of outer administrative units.
     * This assumes the name is formatted with outer unit names separated by commas (", ").
     * If the name format is different, consider adjusting the splitting logic.
     */
    val firstName: String get() {
        return name.split(", ").getOrNull(index = 0) ?: name
    }

    /**
     * Handles active regions (regions that are visible and not unchecked by a user) from the
     * cartographic boundary delimited by a bounding box.
     *
     * @param boundingBox the delimited area used to filter the regions.
     * @return            the list of regions that are not unchecked by the user and it's inside the
     *                    bounds.
     */
    fun activeCartographicBoundaryRegionsWithin(boundingBox: BoundingBox): List<Region> {
        val cartographicBoundary = cartographicBoundary ?: return emptyList()
        return cartographicBoundary.activeRegionsWithin(boundingBox)
    }

    /**
     * @return the more readable stringified version of the administrative unit for debug, test, and
     *         log purposes
     */
    override fun toString(): String {
        val imagesSize = images.size
        val subAdministrativeUnitsSize = subAdministrativeUnits.size
        val imagesSizeString = "$imagesSize ${if (imagesSize > 1) "images" else "image"}"
        val subAdministrativeUnitsSizeString = "$subAdministrativeUnitsSize ${if (subAdministrativeUnitsSize > 1) "subAdministrativeUnits" else "subAdministrativeUnit"}"
        return "($name, $administrativeLevel, $cartographicBoundary, $subAdministrativeUnitsSizeString, $imagesSizeString)"
    }
}