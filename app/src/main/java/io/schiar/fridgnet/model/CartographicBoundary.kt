package io.schiar.fridgnet.model

/**
 * Represents a cartographic boundary on a map, typically the outline of an administrative unit.
 *
 * @property id                     The database ID of the cartographic boundary (defaults to 0).
 * @property administrativeUnitName The name of the administrative unit associated with this
 * boundary. Refer to [AdministrativeUnitName] for details on how the name is constructed based on
 * the administrative level (e.g., city, county, state) for flexibility with different regional
 * structures.
 * @property regions                A list of [Region] objects that define the outline of the
 * boundary. Some administrative units may have disconnected territories, hence the list.
 * @property boundingBox            The BoundingBox object encompassing the entire cartographic
 * boundary (including all regions).
 * @property zIndex                 The layer order on the map where the boundary should be drawn
 * (higher zIndex means drawn on top of others).
 * @property administrativeLevel    The administrative level of this unit
 * (e.g., state, county, city).
 */
data class CartographicBoundary(
    val id: Long = 0,
    val administrativeUnitName: AdministrativeUnitName,
    val regions: List<Region>,
    val boundingBox: BoundingBox,
    val zIndex: Float,
    val administrativeLevel: AdministrativeLevel,
) {
    /**
     * @return the administrative level concatenated with its administrative unit name.
     */
    val administrationLevelWithName: String get() {
        return administrativeLevel.with(administrativeUnitName = administrativeUnitName)
    }

    /**
     * @return the administrative unit name according to its administrative level.
     */
    val administrativeUnitNameString: String get() {
        return administrativeUnitName.toString(administrativeLevel = administrativeLevel)
    }

    /**
     * Returns a list of all active regions that are completely within the specified bounding box.
     *
     * @param boundingBox The bounding box defining the area of interest.
     * @return            A list of active regions whose bounding boxes are entirely contained
     *                    within the provided bounding box.
     */
    fun activeRegionsWithin(boundingBox: BoundingBox): List<Region> {
        return regions.filter { region ->
            val regionBoundingBox = region.boundingBox
            val isRegionActive = region.active
            val isRegionWithinBoundingBox = boundingBox.contains(regionBoundingBox)
            isRegionActive && isRegionWithinBoundingBox
        }
    }

    /**
     * Creates a new cartographic boundary with all regions deactivated except
     * the one with the largest polygon (based on the number of geoLocations).
     * Deactivated regions will be excluded from the final map visualization.
     *
     * @return A new cartographic boundary with all regions except the "main" region switched off
     * (deactivated).
     *
     * This method uses the `switch()` method (see documentation for details) to deactivate regions.
     */
    fun allRegionsSwitched(): CartographicBoundary {
        val sortedRegions = regions.sortedBy { region -> region.polygon.geoLocations.size }
            .asReversed()
        return CartographicBoundary(
            id = id,
            administrativeUnitName = administrativeUnitName,
            regions = sortedRegions
                .mapIndexed { index, region ->
                    if (index == 0) region else region.switch()
                },
            boundingBox = boundingBox,
            zIndex = zIndex,
            administrativeLevel = administrativeLevel
        ).updateBoundingBox()
    }

    /**
     * Creates a new cartographic boundary with the region at the specified index switched off
     * (deactivated). Deactivated regions will be excluded from the final map visualization.
     *
     * This method uses the `switch()` method (see documentation for details) to deactivate the
     * region.
     *
     * @param index The index of the region to be switched off.
     * @return A new cartographic boundary with the specified region switched off.
     */
    fun regionSwitchedAt(index: Int): CartographicBoundary {
        val mutableRegions = regions.toMutableList()
        mutableRegions[index] = regions[index].switch()
        return CartographicBoundary(
            id = id,
            administrativeUnitName = administrativeUnitName,
            regions = mutableRegions,
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

    /**
     * @return the more readable stringified version of the cartographic boundary for debug, test,
     *         and log purposes
     */
    override fun toString(): String {
        val regionSize = regions.size
        val regionSizeName = "$regionSize ${if (regions.size == 1) "region" else "regions"}"
        val vertices = regions.map { it.polygon.geoLocations.size }.reduce { acc, i -> acc + i }
        return "($administrativeUnitNameString, $regionSizeName, $vertices vertices)"
    }
}