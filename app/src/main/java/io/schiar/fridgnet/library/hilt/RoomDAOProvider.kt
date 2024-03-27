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

@Module
@InstallIn(SingletonComponent::class)
object RoomDAOProvider {
    @Provides
    fun provideImageDAO(@ApplicationContext context: Context): ImageDAO {
        return FridgnetDatabase.getDatabase(context).imageDAO()
    }

    @Provides
    fun provideAdministrativeUnitNameDAO(
        @ApplicationContext context: Context
    ): AdministrativeUnitNameDAO {
        return FridgnetDatabase.getDatabase(context).administrativeUnitNameDAO()
    }

    @Provides
    fun provideCartographicBoundaryDAO(
        @ApplicationContext context: Context
    ): CartographicBoundaryDAO {
        return FridgnetDatabase.getDatabase(context).cartographicBoundaryDAO()
    }
}