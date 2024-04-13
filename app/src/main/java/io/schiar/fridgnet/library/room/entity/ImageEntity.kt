package io.schiar.fridgnet.library.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * The class representation of the entity 'Image' from the database.
 * This entity stores information about an image.
 *
 * @property id (Long, auto-generated) The unique identifier for the image within the database.
 * @property uri         (String) The Android string uri of the image.
 * @property byteArray   (ByteArray) The raw image pixel data as a byte array.
 * @property date        (Long) The milliseconds since epoch (1970-01-01T00:00:00Z) of when the
 * photo was taken.
 * @property geoLocationID (Long) The geo location entity ID of where the photo was taken
 * (foreign key).
 */
@Entity(tableName = "Image")
data class ImageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val uri: String,
    val byteArray: ByteArray,
    val date: Long,
    val geoLocationID: Long
) {
    /**
     * Checks if two Image objects are equal.
     * Two images are considered equal if they have the same:
     *  - uri
     *  - byteArray content
     *  - date taken
     *  - geoLocationID
     *
     * @param other The other Image object to compare with.
     * @return true if the Image objects are equal, false otherwise.
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ImageEntity

        if (id != other.id) return false
        if (uri != other.uri) return false
        if (!byteArray.contentEquals(other.byteArray)) return false
        if (date != other.date) return false
        return geoLocationID == other.geoLocationID
    }

    /**
     * Calculates a hash code for this Image object.
     * This hash code is based on the [uri], [byteArray] content, [date] taken, and [geoLocationID].
     * The hash code is used for efficient storage and retrieval in hash-based collections.
     *
     * @return an integer hash code value.
     */
    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + uri.hashCode()
        result = 31 * result + byteArray.contentHashCode()
        result = 31 * result + date.hashCode()
        result = 31 * result + geoLocationID.hashCode()
        return result
    }
}