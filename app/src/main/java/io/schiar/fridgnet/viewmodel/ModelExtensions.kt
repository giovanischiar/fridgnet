package io.schiar.fridgnet.viewmodel

import android.net.Uri
import io.schiar.fridgnet.model.Image
import io.schiar.fridgnet.view.viewdata.ImageViewData
import io.schiar.fridgnet.view.viewdata.LocationViewData

fun Image.toViewData(): ImageViewData {
    val (uri, date, location) = this
    val (latitude, longitude) = location
    return ImageViewData(
        uri = Uri.parse(uri),
        date = date.toString(),
        location = LocationViewData(
            lat = latitude.toString(),
            lng = longitude.toString()
        )
    )
}

fun List<Image>.toViewData(): List<ImageViewData> {
    return map { it.toViewData() }
}

fun Map<String, Image>.toViewData(): Map<Uri, ImageViewData> {
    return entries.associate { Uri.parse(it.key) to it.value.toViewData() }
}