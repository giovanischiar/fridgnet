package io.schiar.fridgnet.library.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Polygon")
data class PolygonEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val holesID: Long? = null
)