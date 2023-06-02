package io.schiar.fridgnet.model.repository.datasource.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Coordinate")
data class CoordinateEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val coordinatesID: Long?,
    val latitude: Double,
    val longitude: Double
)