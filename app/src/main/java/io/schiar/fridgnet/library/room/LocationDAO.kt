package io.schiar.fridgnet.library.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import io.schiar.fridgnet.library.room.entity.CoordinateEntity
import io.schiar.fridgnet.library.room.entity.LocationEntity
import io.schiar.fridgnet.library.room.entity.PolygonEntity
import io.schiar.fridgnet.library.room.entity.RegionEntity
import io.schiar.fridgnet.library.room.relationentity.LocationWithRegions
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationDAO {
    @Insert(onConflict = REPLACE)
    suspend fun insert(locationEntity: LocationEntity): Long

    @Insert(onConflict = REPLACE)
    suspend fun insert(regionEntity: RegionEntity): Long

    @Insert(onConflict = REPLACE)
    suspend fun insert(polygonEntity: PolygonEntity): Long

    @Insert(onConflict = REPLACE)
    suspend fun insertCoordinates(coordinateEntities: List<CoordinateEntity>): List<Long>

    @Transaction
    @Query("SELECT * FROM Location")
    fun selectLocationsWithRegions(): Flow<List<LocationWithRegions>>

    @Update
    suspend fun update(locationEntity: LocationEntity)

    @Update
    suspend fun update(regionEntity: RegionEntity)

    @Query(
        "SELECT * FROM Location WHERE " +
                "Location.locality is :locality AND " +
                "Location.subAdminArea is :subAdminArea AND " +
                "Location.adminArea is :adminArea AND " +
                "Location.countryName is :countryName "
    )
    suspend fun selectLocationWithRegionsByAddress(
        locality: String?,
        subAdminArea: String?,
        adminArea: String?,
        countryName: String?
    ): LocationWithRegions?
}