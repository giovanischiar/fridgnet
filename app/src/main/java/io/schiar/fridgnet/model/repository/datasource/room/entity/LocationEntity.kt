package io.schiar.fridgnet.model.repository.datasource.room.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import io.schiar.fridgnet.model.Address
import io.schiar.fridgnet.model.AdministrativeUnit

@Entity(tableName = "Location")
data class LocationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @Embedded
    val address: Address,
    val administrativeUnit: AdministrativeUnit,
    @Embedded(prefix = "boundingBoxSouthwest_")
    var boundingBoxSouthwest: CoordinateEntity,
    @Embedded(prefix = "boundingBoxNortheast_")
    var boundingBoxNortheast: CoordinateEntity,
    val zIndex: Float
)