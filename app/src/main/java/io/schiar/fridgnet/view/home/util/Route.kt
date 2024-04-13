package io.schiar.fridgnet.view.home.util

/**
 * Enum used to name the routes used to navigate between screens.
 * @param id the name of the id of the route
 */
enum class Route(val id: String) {
    /** The Administrative Units Screen Route */
    ADMINISTRATIVE_UNITS(id = "administrative-units"),
    /** The Administrative Unit Screen Route */
    ADMINISTRATIVE_UNIT(id = "administrative-unit"),
    /** The Regions and Images Screen Route */
    REGIONS_AND_IMAGES(id = "regions-and-images"),
    /** The Home Screen Route */
    HOME(id = "home"),
    /** The Regions From a Cartographic Boundary Screen Route */
    REGIONS_FROM_CARTOGRAPHIC_BOUNDARY(id = "regions-from-cartographic-boundary")
}