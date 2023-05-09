package io.schiar.fridgnet.viewmodel

import android.net.Uri
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import io.schiar.fridgnet.model.*
import io.schiar.fridgnet.view.viewdata.*

fun Image.toViewData(): ImageViewData {
    val (uri, date, location) = this
    val (latitude, longitude) = location
    return ImageViewData(
        uri = Uri.parse(uri),
        date = date.toString(),
        coordinate = CoordinateViewData(
            lat = latitude.toString(),
            lng = longitude.toString()
        )
    )
}

fun List<Image>.toViewData(): List<ImageViewData> {
    return map { it.toViewData() }
}

fun Map<Address, List<Image>>.toAddressImageListViewData(): Map<Address, List<ImageViewData>> {
    return mapValues { it.value.toViewData() }
}

fun Map<Address, Location>.toAddressLocationViewData(): Map<Address, LocationViewData> {
    return mapValues {
        when (it.value) {
            is LineStringLocation -> {
                (it.value as LineStringLocation).toViewData()
            }
            is PolygonLocation -> {
                (it.value as PolygonLocation).toViewData()
            }
            else -> {
                (it.value as MultiPolygonLocation).toViewData()
            }
        }
    }
}

fun Map<String, Image>.toListImagesViewData(): List<ImageViewData> {
    return values.toList().toViewData()
}

fun List<Double>.toCoordinate(): Coordinate {
    return Coordinate(latitude = this[1], longitude = this[0])
}

fun List<List<Double>>.toLineStringCoordinates(): List<Coordinate> {
    return map { it.toCoordinate() }
}

fun List<List<List<Double>>>.toPolygonCoordinates(): List<List<Coordinate>> {
    return map { it.toLineStringCoordinates() }
}

fun List<List<List<List<Double>>>>.toMultiPolygonCoordinates(): List<List<List<Coordinate>>> {
    return map { it.toPolygonCoordinates() }
}

fun Coordinate.toLatLng(): LatLng {
    return LatLng(this.latitude, this.longitude)
}

fun List<Coordinate>.toLineStringLatLng(): List<LatLng> {
    return map { it.toLatLng() }
}

fun List<List<Coordinate>>.toPolygonLatLng(): List<List<LatLng>> {
    return map { it.toLineStringLatLng() }
}

fun List<List<List<Coordinate>>>.toMultiPolygonLatLng(): List<List<List<LatLng>>> {
    return map { it.toPolygonLatLng() }
}

fun List<String>.toBoundingBox(): BoundingBox {
    val ne = Coordinate(latitude = this[0].toDouble(), longitude = this[2].toDouble())
    val sw = Coordinate(latitude = this[1].toDouble(), longitude = this[3].toDouble())
    return BoundingBox(ne = ne, sw = sw)
}

fun BoundingBox.toLatLngBounds(): LatLngBounds {
    return LatLngBounds(this.ne.toLatLng(), this.sw.toLatLng())
}

fun LineStringLocation.toViewData(): LineStringLocationViewData {
    return LineStringLocationViewData(
        region = this.region.toLineStringLatLng(),
        boundingBox = this.boundingBox.toLatLngBounds()
    )
}

fun PolygonLocation.toViewData(): PolygonLocationViewData {
    return PolygonLocationViewData(
        region = this.region.toPolygonLatLng(),
        boundingBox = this.boundingBox.toLatLngBounds()
    )
}

fun MultiPolygonLocation.toViewData(): MultiPolygonLocationViewData {
    return MultiPolygonLocationViewData(
        region = this.region.toMultiPolygonLatLng(),
        boundingBox = this.boundingBox.toLatLngBounds()
    )
}

fun android.location.Address.toAddress(): Address {
    return Address(
        locality = this.locality,
        subAdminArea = this.subAdminArea,
        adminArea = this.adminArea,
        countryName = this.countryName
    )
}