package io.schiar.fridgnet.model

data class CartographicBoundary(
    val id: Long = 0,
    val administrativeUnitName: AdministrativeUnitName,
    val regions: List<Region>,
    val boundingBox: BoundingBox,
    val zIndex: Float,
    val administrativeLevel: AdministrativeLevel,
) {
    fun administrativeUnitNameString(): String {
        return administrativeUnitName.toString(administrativeLevel = administrativeLevel)
    }

    fun switchAll(): CartographicBoundary {
        return CartographicBoundary(
            id = id,
            administrativeUnitName = administrativeUnitName,
            regions = regions
                .sortedBy { region -> region.polygon.geoLocations.size }
                .asReversed()
                .mapIndexed { index, region ->
                    if (index == 0) region else region.switch()
                },
            boundingBox = boundingBox,
            zIndex = zIndex,
            administrativeLevel = administrativeLevel
        ).updateBoundingBox()
    }

    fun switchRegionAt(index: Int): CartographicBoundary {
        return switch(region = regions[index])
    }

    private fun switch(region: Region): CartographicBoundary {
        val mutableRegions = regions.toMutableList()
        val index = regions.indexOf(region)
        mutableRegions[index] = regions[index].switch()
        return CartographicBoundary(
            id = id,
            administrativeUnitName = administrativeUnitName,
            regions = mutableRegions.toList(),
            boundingBox = boundingBox,
            zIndex = zIndex,
            administrativeLevel = administrativeLevel
        ).updateBoundingBox()
    }

    private fun updateBoundingBox(): CartographicBoundary {
        return CartographicBoundary(
            id = id,
            administrativeUnitName = administrativeUnitName,
            regions = regions,
            boundingBox = regions
                .filter { it.active }
                .map { it.boundingBox }
                .reduce { boundingBox, otherBoundingBox ->
                    boundingBox + otherBoundingBox
                },
            zIndex = zIndex,
            administrativeLevel = administrativeLevel
        )
    }

    override fun toString(): String {
        val regionSize = regions.size
        val regionSizeName = "$regionSize ${if (regions.size == 1) "region" else "regions"}"
        return "(${administrativeUnitNameString()}, $regionSizeName)"
    }
}