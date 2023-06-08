package io.schiar.fridgnet.model.repository.location.datasource.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import io.schiar.fridgnet.model.repository.location.datasource.room.entity.CoordinateEntity
import io.schiar.fridgnet.model.repository.location.datasource.room.entity.LocationEntity
import io.schiar.fridgnet.model.repository.location.datasource.room.entity.PolygonEntity
import io.schiar.fridgnet.model.repository.location.datasource.room.entity.RegionEntity

@Database(
    entities = [
        CoordinateEntity::class,
        LocationEntity::class,
        PolygonEntity::class,
        RegionEntity::class,
    ],
    version = 1,
    exportSchema = false
)
abstract class LocationDatabase: RoomDatabase() {
    abstract fun locationDAO(): LocationDAO

    companion object {
        @Volatile
        private var Instance: LocationDatabase? = null

        fun getDatabase(context: Context): LocationDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context = context,
                    klass = LocationDatabase::class.java,
                    name = "location_database"
                ).fallbackToDestructiveMigration().build().also { Instance = it }
            }
        }
    }
}