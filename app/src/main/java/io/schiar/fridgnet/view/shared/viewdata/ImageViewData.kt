package io.schiar.fridgnet.view.shared.viewdata

data class ImageViewData(
    val uri: String,
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
        return geoLocation == other.geoLocation
    }

    override fun hashCode(): Int {
        var result = uri.hashCode()
        result = 31 * result + byteArray.contentHashCode()
        result = 31 * result + date.hashCode()
        result = 31 * result + geoLocation.hashCode()
        return result
    }
}