package io.schiar.fridgnet.model

data class Location(
    val id: Long = 0,
    val address: Address,
    val regions: List<Region>,
    val boundingBox: BoundingBox,
    val zIndex: Float,
    val administrativeUnit: AdministrativeUnit,
) {
    fun addressName(): String {
        return address.name(administrativeUnit = administrativeUnit)
    }

    fun updateAddress(address: Address): Location {
        return Location(
            id = id,
            address = address,
            regions = regions,
            boundingBox = boundingBox,
            zIndex = zIndex,
            administrativeUnit = administrativeUnit
        )
    }

    fun switchAll(): Location {
        return Location(
            id = id,
            address = address,
            regions = regions
                .sortedBy { region -> region.polygon.coordinates.size }
                .asReversed()
                .mapIndexed { index, region ->
                    if (index == 0) region else region.switch()
                },
            boundingBox = boundingBox,
            zIndex = zIndex,
            administrativeUnit = administrativeUnit
        ).updateBoundingBox()
    }

    fun switchRegionAt(index: Int): Location {
        return switch(region = regions[index])
    }

    private fun switch(region: Region): Location {
        val mutableRegions = regions.toMutableList()
        val index = regions.indexOf(region)
        mutableRegions[index] = regions[index].switch()
        return Location(
            id = id,
            address = address,
            regions = mutableRegions.toList(),
            boundingBox = boundingBox,
            zIndex = zIndex,
            administrativeUnit = administrativeUnit
        ).updateBoundingBox()
    }

    private fun updateBoundingBox(): Location {
        return Location(
            id = id,
            address = address,
            regions = regions,
            boundingBox = regions
                .filter { it.active }
                .map { it.boundingBox }
                .reduce { boundingBox, otherBoundingBox ->
                    boundingBox + otherBoundingBox
                },
            zIndex = zIndex,
            administrativeUnit = administrativeUnit
        )
    }
}