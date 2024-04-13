package io.schiar.fridgnet.view.shared.viewdata

/**
 * The data necessary for displaying the GeoLocationViewData on the View.
 *
 * @property latitude  the y-axis coordinate of the location in degrees, ranging from -90
 * (South Pole) to 90 (North Pole).
 * @property longitude the x-axis coordinate of the location in degrees, ranging from -180 (West) to
 * 180 (East).
 */
data class GeoLocationViewData(val latitude: Double = 0.0, val longitude: Double = 0.0)