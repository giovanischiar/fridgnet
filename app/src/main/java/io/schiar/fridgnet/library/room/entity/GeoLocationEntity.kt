package io.schiar.fridgnet.library.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "GeoLocation")
data class GeoLocationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val geoLocationsID: Long? = null,
    val addressGeoLocationsID: Long? = null,
    val latitude: Double,
    val longitude: Double
)