package io.schiar.fridgnet.library.hilt

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.schiar.fridgnet.library.room.AdministrativeUnitNameDAO
import io.schiar.fridgnet.library.room.CartographicBoundaryDAO
import io.schiar.fridgnet.library.room.FridgnetDatabase
import io.schiar.fridgnet.library.room.ImageDAO

/**
 * Provides room-generated implementations of data access objects (DAOs) used to interact with the
 * persistence layer. This module is installed in the SingletonComponent.
 */
@Module
@InstallIn(SingletonComponent::class)
object RoomDAOProvider {
    /**
     * Provides the DAO for interacting with the Image table in the database.
     *
     * @param context the ApplicationContext provided by Hilt from Android
     * @return the room-generated implementation of the [ImageDAO] interface.
     */
    @Provides
    fun provideImageDAO(@ApplicationContext context: Context): ImageDAO {
        return FridgnetDatabase.getDatabase(context).imageDAO()
    }

    /**
     * Provides the DAO for interacting with the AdministrativeUnitName table in the database.
     *
     * @param context the ApplicationContext provided by Hilt from Android
     * @return the room-generated implementation of the [AdministrativeUnitNameDAO] interface.
     */
    @Provides
    fun provideAdministrativeUnitNameDAO(
        @ApplicationContext context: Context
    ): AdministrativeUnitNameDAO {
        return FridgnetDatabase.getDatabase(context).administrativeUnitNameDAO()
    }

    /**
     * Provides the DAO for interacting with the CartographicBoundary table in the database.
     *
     * @param context the ApplicationContext provided by Hilt from Android
     * @return the room-generated implementation of the [CartographicBoundaryDAO] interface.
     */
    @Provides
    fun provideCartographicBoundaryDAO(
        @ApplicationContext context: Context
    ): CartographicBoundaryDAO {
        return FridgnetDatabase.getDatabase(context).cartographicBoundaryDAO()
    }
}