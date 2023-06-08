package io.schiar.fridgnet.model.repository.location.datasource.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import androidx.room.Update
import io.schiar.fridgnet.model.repository.location.datasource.room.entity.CoordinateEntity
import io.schiar.fridgnet.model.repository.location.datasource.room.entity.LocationEntity
import io.schiar.fridgnet.model.repository.location.datasource.room.entity.PolygonEntity
import io.schiar.fridgnet.model.repository.location.datasource.room.entity.RegionEntity
import io.schiar.fridgnet.model.repository.location.datasource.room.relationentity.LocationWithRegions

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