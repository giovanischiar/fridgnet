package io.schiar.fridgnet.library.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * The class representation of the entity 'Polygon' from the database.
 * This entity stores information about a polygon
 *
 * @property id (Long, auto-generated) The unique identifier for the image within the database.
 * @property holesID used for Regions to in indicate a polygon as a hole inside the Region
 */
@Entity(tableName = "Polygon")
data class PolygonEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val holesID: Long? = null
)