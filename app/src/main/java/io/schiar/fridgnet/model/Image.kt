package io.schiar.fridgnet.model

/**
 * Represents an image used in the map screens.
 *
 * @property uri         The Android string uri of the image.
 * @property byteArray   The [ByteArray] containing the raw image pixel data.
 * @property date        The milliseconds since epoch (1970-01-01T00:00:00Z) of when the photo was
 * taken.
 * @property geoLocation The coordinates of where the photo was taken.
 */
data class Image(
    val uri: String,
    val byteArray: ByteArray,
    val date: Long,
    val geoLocation: GeoLocation
) {

    /**
     * Checks if two Image objects are equal.
     * Two images are considered equal if they have the same:
     *  - uri
     *  - byteArray content
     *  - date taken
     *  - geoLocation
     *
     * @param other The other Image object to compare with.
     * @return true if the Image objects are equal, false otherwise.
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Image

        if (uri != other.uri) return false
        if (!byteArray.contentEquals(other.byteArray)) return false
        if (date != other.date) return false
        return geoLocation == other.geoLocation
    }

    /**
     * Calculates the hash code for this object based on its member variables.
     *
     * The hash code is a unique identifier for the object within the context of a hash table.
     * Kotlin by default doesn't include byte arrays in the generated hash code for data classes.
     * This custom implementation combines the hash codes of the following member variables:
     *   * uri
     *   * byteArray
     *   * date
     *   * geoLocation
     *
     * @return the hash code value for this object.
     */
    override fun hashCode(): Int {
        var result = uri.hashCode()
        result = 31 * result + byteArray.contentHashCode()
        result = 31 * result + date.hashCode()
        result = 31 * result + geoLocation.hashCode()
        return result
    }
}