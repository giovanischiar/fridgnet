package io.schiar.fridgnet.library.room.relationentity

import androidx.room.Embedded
import androidx.room.Relation
import io.schiar.fridgnet.library.room.entity.GeoLocationEntity
import io.schiar.fridgnet.library.room.entity.PolygonEntity

data class PolygonWithGeoLocations(
    @Embedded
    val polygon: PolygonEntity,
    @Relation(parentColumn = "id", entityColumn = "geoLocationsID")
    val getLocationEntities: List<GeoLocationEntity>
)