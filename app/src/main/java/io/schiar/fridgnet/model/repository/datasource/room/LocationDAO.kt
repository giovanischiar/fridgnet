package io.schiar.fridgnet.model.repository.datasource.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import io.schiar.fridgnet.model.repository.datasource.room.entity.*
import io.schiar.fridgnet.model.repository.datasource.room.relationentity.LocationWithRegions

@Dao
abstract class LocationDAO {
    @Insert(onConflict = REPLACE)
    abstract fun insert(locationEntity: LocationEntity): Long

    @Insert(onConflict = REPLACE)
    abstract fun insert(regionEntity: RegionEntity): Long

    @Insert(onConflict = REPLACE)
    abstract fun insert(polygonEntity: PolygonEntity): Long

    @Insert(onConflict = REPLACE)
    abstract fun insertCoordinates(coordinateEntities: List<CoordinateEntity>): List<Long>

    @Query("SELECT * FROM Location")
    abstract fun selectLocationsWithRegions(): List<LocationWithRegions>
}