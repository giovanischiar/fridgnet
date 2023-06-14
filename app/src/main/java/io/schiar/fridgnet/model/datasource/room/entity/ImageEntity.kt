package io.schiar.fridgnet.model.datasource.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Image")
data class ImageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val uri: String,
    val date: Long,
    val coordinateID: Long
)