package io.schiar.fridgnet.model

data class Location(
    val address: Address,
    val regions: List<Region>,
    val boundingBox: BoundingBox,
    val zIndex: Float
) {
    fun switchAll(): Location {
        return Location(
            address = address,
            regions = regions
                .sortedBy { region -> region.polygon.coordinates.size }
                .asReversed()
                .mapIndexed { index, region -> if (index == 0) region else region.switch()
            },
            boundingBox = boundingBox,
            zIndex = zIndex
        ).updateBoundingBox()
    }
    fun switch(region: Region): Location {
        val mutableRegions = regions.toMutableList()
        val index = regions.indexOf(region)
        mutableRegions[index] = regions[index].switch()
        return Location(
            address = address,
            regions = mutableRegions.toList(),
            boundingBox = boundingBox,
            zIndex = zIndex
        ).updateBoundingBox()
    }

    private fun updateBoundingBox(): Location {
        return Location(
            address = address,
            regions = regions,
            boundingBox = regions
                .filter { it.active }
                .map { it.boundingBox }
                .reduce { boundingBox, otherBoundingBox ->
                    boundingBox + otherBoundingBox
            },
            zIndex = zIndex
        )
    }
}