package io.schiar.fridgnet.viewmodel.util

import io.schiar.fridgnet.model.BoundingBox
import io.schiar.fridgnet.model.GeoLocation
import io.schiar.fridgnet.view.viewdata.BoundingBoxViewData
import io.schiar.fridgnet.view.viewdata.GeoLocationViewData

//view.viewdata.GeoLocationViewData
fun GeoLocationViewData.toGeoLocation(): GeoLocation {
    return GeoLocation(latitude = latitude, longitude = longitude)
}

// view.viewdata.BoundingBoxViewData
fun BoundingBoxViewData.toBoundingBox(): BoundingBox {
    return BoundingBox(southwest = southwest.toGeoLocation(), northeast = northeast.toGeoLocation())
}