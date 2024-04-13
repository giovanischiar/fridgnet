package io.schiar.fridgnet.view.shared.viewdata

/**
 * The data necessary for displaying the Polygon on the View.
 *
 * @property geoLocations  The list of GeoLocation objects defining the vertices of the polygon.
 * **Note:** The list should be closed (first and last point should be the same) to represent a
 * complete polygon on a map.
 */
data class PolygonViewData(val geoLocations: List<GeoLocationViewData>)