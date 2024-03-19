package io.schiar.fridgnet.model

data class Location(
    val id: Long = 0,
    val address: Address,
    val regions: List<Region>,
    val boundingBox: BoundingBox,
    val zIndex: Float,
    val administrativeLevel: AdministrativeLevel,
) {
    fun addressName(): String {
        return address.name(administrativeLevel = administrativeLevel)
    }

    fun updateAddress(address: Address): Location {
        return Location(
            id = id,
            address = address,
            regions = regions,
            boundingBox = boundingBox,
            zIndex = zIndex,
            administrativeLevel = administrativeLevel
        )
    }

    fun switchAll(): Location {
        return Location(
            id = id,
            address = address,
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
            administrativeLevel = administrativeLevel
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
            administrativeLevel = administrativeLevel
        )
    }

    override fun toString(): String {
        val regionSize = regions.size
        val regionSizeName = "$regionSize ${if (regions.size == 1) "region" else "regions"}"
        return "(${addressName()}, $regionSizeName)"
    }
}