package io.schiar.fridgnet.model.datasource.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import io.schiar.fridgnet.model.datasource.room.entity.*

@Database(
    entities = [
        AddressEntity::class,
        CoordinateEntity::class,
        LocationEntity::class,
        PolygonEntity::class,
        RegionEntity::class,
    ],
    version = 1,
    exportSchema = false
)
abstract class FridgnetDatabase: RoomDatabase() {
    abstract fun addressDAO(): AddressDAO
    abstract fun locationDAO(): LocationDAO

    companion object {
        @Volatile
        private var Instance: FridgnetDatabase? = null

        fun getDatabase(context: Context): FridgnetDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context = context,
                    klass = FridgnetDatabase::class.java,
                    name = "location_database"
                ).fallbackToDestructiveMigration().build().also { Instance = it }
            }
        }
    }
}