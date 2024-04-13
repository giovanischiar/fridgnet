package io.schiar.fridgnet.viewmodel.util

import io.schiar.fridgnet.model.BoundingBox
import io.schiar.fridgnet.model.GeoLocation
import io.schiar.fridgnet.view.shared.viewdata.BoundingBoxViewData
import io.schiar.fridgnet.view.shared.viewdata.GeoLocationViewData

/**
 * Converts the [GeoLocationViewData] into [GeoLocation].
 *
 * @return the [GeoLocation] converted
 */
fun GeoLocationViewData.toGeoLocation(): GeoLocation {
    return GeoLocation(latitude = latitude, longitude = longitude)
}

/**
 * Converts the [BoundingBoxViewData] into [BoundingBox].
 *
 * @return the [BoundingBox] converted
 */
fun BoundingBoxViewData.toBoundingBox(): BoundingBox {
    return BoundingBox(southwest = southwest.toGeoLocation(), northeast = northeast.toGeoLocation())
}