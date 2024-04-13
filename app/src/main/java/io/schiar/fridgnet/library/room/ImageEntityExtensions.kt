package io.schiar.fridgnet.library.room

import io.schiar.fridgnet.library.room.entity.ImageEntity
import io.schiar.fridgnet.model.Image

/**
 * Converts Image into Image Entity using the geoLocationID foreign key to associate to a
 * geolocation
 *
 * @param geoLocationID the geo location id provided
 * @return the Image Entity
 */
fun Image.toImageEntity(geoLocationID: Long): ImageEntity {
    return ImageEntity(uri = uri, byteArray = byteArray, date = date, geoLocationID = geoLocationID)
}