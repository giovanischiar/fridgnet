package io.schiar.fridgnet.model.datasource.room

import androidx.room.*
import androidx.room.OnConflictStrategy.Companion.REPLACE
import io.schiar.fridgnet.model.datasource.room.entity.CoordinateEntity
import io.schiar.fridgnet.model.datasource.room.entity.LocationEntity
import io.schiar.fridgnet.model.datasource.room.entity.PolygonEntity
import io.schiar.fridgnet.model.datasource.room.entity.RegionEntity
import io.schiar.fridgnet.model.datasource.room.relationentity.LocationWithRegions

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

    @Transaction
    @Query("SELECT * FROM Location")
    abstract fun selectLocationsWithRegions(): List<LocationWithRegions>

    @Update
    abstract fun update(locationEntity: LocationEntity)

    @Update
    abstract fun update(regionEntity: RegionEntity)

    @Query(
        "SELECT * FROM Location WHERE " +
        "Location.locality is :locality AND " +
        "Location.subAdminArea is :subAdminArea AND " +
        "Location.adminArea is :adminArea AND " +
        "Location.countryName is :countryName "
    )
    abstract fun selectLocationWithRegionsByAddress(
        locality: String?,
        subAdminArea: String?,
        adminArea: String?,
        countryName: String?
    ): LocationWithRegions?
}