package io.schiar.fridgnet.model

data class AdministrativeUnit(
    val name: String,
    val administrativeLevel: AdministrativeLevel,
    var cartographicBoundary: CartographicBoundary? = null,
    val subAdministrativeUnitNames: List<AdministrativeUnit> = emptyList(),
    val images: MutableList<Image> = mutableListOf()
) {
    fun with(images: List<Image>): AdministrativeUnit {
        return AdministrativeUnit(
            name = name,
            administrativeLevel = administrativeLevel,
            cartographicBoundary = cartographicBoundary,
            subAdministrativeUnitNames = subAdministrativeUnitNames,
            images = images.toMutableList()
        )
    }
}