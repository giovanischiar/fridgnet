package io.schiar.fridgnet.view.shared.viewdata

/**
 * The data necessary for displaying a bounding box on the view. This information is primarily
 * used for zoom purposes.
 *
 * @property southwest the geographic location data for the southwestern corner of the bounding box.
 * @property northeast the geographic location data for the northeastern corner of the bounding box.
 */
data class BoundingBoxViewData(
    val southwest: GeoLocationViewData, val northeast: GeoLocationViewData
)