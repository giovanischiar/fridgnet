package io.schiar.fridgnet.library.room.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "CartographicBoundary")
data class CartographicBoundaryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val administrativeUnitNameCartographicBoundariesID: Long,
    val administrativeLevel: String,
    @Embedded(prefix = "boundingBoxSouthwest_")
    var boundingBoxSouthwest: GeoLocationEntity,
    @Embedded(prefix = "boundingBoxNortheast_")
    var boundingBoxNortheast: GeoLocationEntity,
    val zIndex: Float
) {
    fun boundingBoxUpdated(
        southwestLatitude: Double,
        southwestLongitude: Double,
        northeastLatitude: Double,
        northeastLongitude: Double
    ): CartographicBoundaryEntity {
        return CartographicBoundaryEntity(
            id = id,
            administrativeUnitNameCartographicBoundariesID = administrativeUnitNameCartographicBoundariesID,
            administrativeLevel = administrativeLevel,
            boundingBoxSouthwest = GeoLocationEntity(
                id = boundingBoxSouthwest.id,
                geoLocationsID = boundingBoxSouthwest.geoLocationsID,
                latitude = southwestLatitude,
                longitude = southwestLongitude
            ),
            boundingBoxNortheast = GeoLocationEntity(
                id = boundingBoxNortheast.id,
                geoLocationsID = boundingBoxNortheast.geoLocationsID,
                latitude = northeastLatitude,
                longitude = northeastLongitude
            ),
            zIndex = zIndex
        )
    }
}