package io.schiar.fridgnet.model

import io.schiar.fridgnet.library.util.IdentitySet

data class AdministrativeUnit(
    val name: String,
    val administrativeLevel: AdministrativeLevel,
    var cartographicBoundary: CartographicBoundary? = null,
    val subAdministrativeUnits: IdentitySet<AdministrativeUnit> = IdentitySet(),
    val images: MutableSet<Image> = mutableSetOf()
) {
    val firstName: String get() {
        return name.split(", ").getOrNull(index = 0) ?: name
    }

    fun with(images: List<Image>): AdministrativeUnit {
        return AdministrativeUnit(
            name = name,
            administrativeLevel = administrativeLevel,
            cartographicBoundary = cartographicBoundary,
            subAdministrativeUnits = subAdministrativeUnits,
            images = images.toMutableSet()
        )
    }

    override fun toString(): String {
        val imagesSize = images.size
        val subAdministrativeUnitsSize = subAdministrativeUnits.size
        val imagesSizeString = "$imagesSize ${if (imagesSize > 1) "images" else "image"}"
        val subAdministrativeUnitsSizeString = "$subAdministrativeUnitsSize ${if (subAdministrativeUnitsSize > 1) "subAdministrativeUnits" else "subAdministrativeUnit"}"
        return "($name, $administrativeLevel, $cartographicBoundary, $subAdministrativeUnitsSizeString, $imagesSizeString)"
    }
}