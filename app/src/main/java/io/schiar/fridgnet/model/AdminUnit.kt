package io.schiar.fridgnet.model

data class AdminUnit(
    val name: String,
    val administrativeLevel: AdministrativeLevel,
    var cartographicBoundary: CartographicBoundary? = null,
    val subAdministrativeUnitNames: List<AdminUnit> = emptyList(),
    val images: MutableList<Image> = mutableListOf()
) {
    fun with(images: List<Image>): AdminUnit {
        return AdminUnit(
            name = name,
            administrativeLevel = administrativeLevel,
            cartographicBoundary = cartographicBoundary,
            subAdministrativeUnitNames = subAdministrativeUnitNames,
            images = images.toMutableList()
        )
    }
}