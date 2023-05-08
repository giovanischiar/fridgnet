package io.schiar.fridgnet.viewmodel

import android.net.Uri
import io.schiar.fridgnet.model.Address
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

fun Map<Address, List<Image>>.toViewData(): Map<Address, List<ImageViewData>> {
    return mapValues { it.value.toViewData() }
}

fun Map<String, Image>.toListImagesViewData(): List<ImageViewData> {
    return values.toList().toViewData()
}