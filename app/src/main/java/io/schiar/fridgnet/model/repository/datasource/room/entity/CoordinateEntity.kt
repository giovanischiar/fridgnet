package io.schiar.fridgnet.model.repository.datasource.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import io.schiar.fridgnet.model.Coordinate

@Entity(tableName = "Coordinate")
data class CoordinateEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val coordinatesID: Long?,
    val latitude: Double,
    val longitude: Double
) {
    fun toCoordinate(): Coordinate {
        return Coordinate(
            latitude = latitude,
            longitude = longitude
        )
    }
}