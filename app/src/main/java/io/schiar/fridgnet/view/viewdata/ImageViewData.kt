package io.schiar.fridgnet.view.viewdata

import android.net.Uri

data class ImageViewData(
    val uri: Uri,
    val byteArray: ByteArray,
    val date: String,
    val geoLocation: GeoLocationViewData
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ImageViewData

        if (uri != other.uri) return false
        if (!byteArray.contentEquals(other.byteArray)) return false
        if (date != other.date) return false
        if (geoLocation != other.geoLocation) return false

        return true
    }

    override fun hashCode(): Int {
        var result = uri.hashCode()
        result = 31 * result + byteArray.contentHashCode()
        result = 31 * result + date.hashCode()
        result = 31 * result + geoLocation.hashCode()
        return result
    }
}