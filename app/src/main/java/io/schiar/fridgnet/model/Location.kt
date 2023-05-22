package io.schiar.fridgnet.model

data class Location(
    val address: Address,
    val administrativeUnit: AdministrativeUnit,
    val regions: List<Region>,
    val boundingBox: BoundingBox
)