package io.schiar.fridgnet.library.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import io.schiar.fridgnet.library.room.entity.AdministrativeUnitEntity
import io.schiar.fridgnet.library.room.entity.CartographicBoundaryEntity
import io.schiar.fridgnet.library.room.entity.GeoLocationEntity
import io.schiar.fridgnet.library.room.entity.ImageEntity
import io.schiar.fridgnet.library.room.entity.PolygonEntity
import io.schiar.fridgnet.library.room.entity.RegionEntity

@Database(
    entities = [
        ImageEntity::class,
        AdministrativeUnitEntity::class,
        GeoLocationEntity::class,
        CartographicBoundaryEntity::class,
        PolygonEntity::class,
        RegionEntity::class,
    ],
    version = 1,
    exportSchema = false
)
abstract class FridgnetDatabase : RoomDatabase() {
    abstract fun imageDAO(): ImageDAO
    abstract fun administrativeUnitDAO(): AdministrativeUnitDAO
    abstract fun cartographicBoundaryDAO(): CartographicBoundaryDAO

    companion object {
        @Volatile
        private var Instance: FridgnetDatabase? = null

        fun getDatabase(context: Context): FridgnetDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context = context,
                    klass = FridgnetDatabase::class.java,
                    name = "fridgnet_database"
                ).fallbackToDestructiveMigration().build().also { Instance = it }
            }
        }
    }
}