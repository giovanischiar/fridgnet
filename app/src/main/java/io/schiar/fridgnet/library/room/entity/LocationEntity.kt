package io.schiar.fridgnet.library.room.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Location")
data class LocationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val addressLocationsID: Long,
    val administrativeUnit: String,
    @Embedded(prefix = "boundingBoxSouthwest_")
    var boundingBoxSouthwest: CoordinateEntity,
    @Embedded(prefix = "boundingBoxNortheast_")
    var boundingBoxNortheast: CoordinateEntity,
    val zIndex: Float
) {
    fun boundingBoxUpdated(
        southwestLatitude: Double,
        southwestLongitude: Double,
        northeastLatitude: Double,
        northeastLongitude: Double
    ): LocationEntity {
        return LocationEntity(
            id = id,
            addressLocationsID = addressLocationsID,
            administrativeUnit = administrativeUnit,
            boundingBoxSouthwest = CoordinateEntity(
                id = boundingBoxSouthwest.id,
                coordinatesID = boundingBoxSouthwest.coordinatesID,
                latitude = southwestLatitude,
                longitude = southwestLongitude
            ),
            boundingBoxNortheast = CoordinateEntity(
                id = boundingBoxNortheast.id,
                coordinatesID = boundingBoxNortheast.coordinatesID,
                latitude = northeastLatitude,
                longitude = northeastLongitude
            ),
            zIndex = zIndex
        )
    }
}