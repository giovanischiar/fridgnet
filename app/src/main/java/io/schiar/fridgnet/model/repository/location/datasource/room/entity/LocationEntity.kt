package io.schiar.fridgnet.model.repository.location.datasource.room.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Location")
data class LocationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val locality: String?,
    val subAdminArea: String?,
    val adminArea: String?,
    val countryName: String?,
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
            locality = locality,
            subAdminArea = subAdminArea,
            adminArea = adminArea,
            countryName = countryName,
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