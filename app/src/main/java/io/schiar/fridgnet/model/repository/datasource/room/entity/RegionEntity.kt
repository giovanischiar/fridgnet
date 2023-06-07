package io.schiar.fridgnet.model.repository.datasource.room.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Region")
data class RegionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val regionsID: Long,
    val polygonID: Long,
    val active: Boolean,
    @Embedded(prefix = "boundingBoxSouthwest_")
    var boundingBoxSouthwest: CoordinateEntity,
    @Embedded(prefix = "boundingBoxNortheast_")
    var boundingBoxNortheast: CoordinateEntity,
    val zIndex: Float
) {
    fun switch(): RegionEntity {
        return RegionEntity(
            id = id,
            regionsID = regionsID,
            polygonID = polygonID,
            active = !active,
            boundingBoxNortheast = boundingBoxNortheast,
            boundingBoxSouthwest = boundingBoxSouthwest,
            zIndex = zIndex
        )
    }
}