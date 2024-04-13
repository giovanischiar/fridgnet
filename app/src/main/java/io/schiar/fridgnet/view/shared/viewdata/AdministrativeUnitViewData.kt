package io.schiar.fridgnet.view.shared.viewdata

/**
 * The data necessary for displaying the AdministrativeUnit on the View.
 *
 * @property name the name of the AdministrativeUnit (e.g., "Los Angeles").
 * @property administrativeLevel the level information of this unit
 * (e.g., "City", "County", "State").
 * @property cartographicBoundary the geographic boundary data associated with this unit, or null
 * if there's no associated boundary.
 * @property subCartographicBoundaries a list of any subdivisions (e.g., districts) within this
 * unit.
 * @property images a list of image data objects for images captured within this unit.
 * @property imagesBoundingBox the bounding box that encloses all of the images within this unit, or
 * null if there are no images.
 */
data class AdministrativeUnitViewData(
    val name: String,
    val administrativeLevel: AdministrativeLevelViewData,
    val cartographicBoundary: CartographicBoundaryViewData?,
    val subCartographicBoundaries: List<CartographicBoundaryViewData>,
    val images: List<ImageViewData>,
    val imagesBoundingBox: BoundingBoxViewData?
)