package io.schiar.fridgnet.model.repository.datasource.room.entity

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
)