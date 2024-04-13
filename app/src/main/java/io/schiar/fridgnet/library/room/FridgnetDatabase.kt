package io.schiar.fridgnet.library.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import io.schiar.fridgnet.library.room.entity.AdministrativeUnitNameEntity
import io.schiar.fridgnet.library.room.entity.CartographicBoundaryEntity
import io.schiar.fridgnet.library.room.entity.GeoLocationEntity
import io.schiar.fridgnet.library.room.entity.ImageEntity
import io.schiar.fridgnet.library.room.entity.PolygonEntity
import io.schiar.fridgnet.library.room.entity.RegionEntity

/**
 * This abstract class represents a Room database for the Fridgnet application.
 * It provides access to various DAOs (Data Access Objects) for interacting with
 * different database entities.
 */
@Database(
    entities = [
        ImageEntity::class,
        AdministrativeUnitNameEntity::class,
        GeoLocationEntity::class,
        CartographicBoundaryEntity::class,
        PolygonEntity::class,
        RegionEntity::class,
    ],
    version = 1,
    exportSchema = false
)
abstract class FridgnetDatabase : RoomDatabase() {
    /**
     * @return the DAO (Data Access Object) for images, providing methods for CRUD operations
     * and other interactions with [ImageEntity] objects in the database.
     */
    abstract fun imageDAO(): ImageDAO

    /**
     * @return the DAO (Data Access Object) for administrative unit names, providing methods for
     * CRUD operations and other interactions with [AdministrativeUnitNameEntity] objects in the
     * database.
     */
    abstract fun administrativeUnitNameDAO(): AdministrativeUnitNameDAO

    /**
     * @return the DAO (Data Access Object) for cartographic boundaries, providing methods for
     * CRUD operations and other interactions with [CartographicBoundaryEntity] objects in the
     * database.
     */
    abstract fun cartographicBoundaryDAO(): CartographicBoundaryDAO

    /**
     * The object that holds the instance to the database.
     */
    companion object {
        @Volatile
        private var Instance: FridgnetDatabase? = null

        /**
         * Provides a thread-safe way to obtain the singleton instance of [FridgnetDatabase].
         *
         * @param context the context provided by the Android System.
         * @return the [FridgnetDatabase] instance.
         */
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