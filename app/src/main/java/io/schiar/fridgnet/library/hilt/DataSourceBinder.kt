package io.schiar.fridgnet.library.hilt

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.schiar.fridgnet.library.room.AdministrativeUnitNameRoomDataSource
import io.schiar.fridgnet.library.room.CartographicBoundaryRoomDataSource
import io.schiar.fridgnet.library.room.ImageRoomDataSource
import io.schiar.fridgnet.model.datasource.AdministrativeUnitDataSource
import io.schiar.fridgnet.model.datasource.AdministrativeUnitNameDataSource
import io.schiar.fridgnet.model.datasource.CartographicBoundaryDataSource
import io.schiar.fridgnet.model.datasource.CurrentAdministrativeUnitDataSource
import io.schiar.fridgnet.model.datasource.CurrentRegionDataSource
import io.schiar.fridgnet.model.datasource.ImageDataSource
import io.schiar.fridgnet.model.datasource.local.AdministrativeUnitLocalDataSource
import io.schiar.fridgnet.model.datasource.local.CurrentLocalAdministrativeUnitDataSource
import io.schiar.fridgnet.model.datasource.local.CurrentRegionLocalDataSource
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DataSourceBinder {
    @Binds
    fun bindImageDataSource(imageRoomDataSource: ImageRoomDataSource): ImageDataSource

    @Binds
    fun bindAdministrativeUnitNameDataSource(
        administrativeUnitNameRoomDataSource: AdministrativeUnitNameRoomDataSource
    ): AdministrativeUnitNameDataSource

    @Binds
    fun bindCartographicBoundaryDataSource(
        cartographicBoundaryRoomDataSource: CartographicBoundaryRoomDataSource
    ): CartographicBoundaryDataSource

    @Binds
    fun bindAdministrativeUnitDataSource(
        administrativeUnitLocalDataSource: AdministrativeUnitLocalDataSource
    ): AdministrativeUnitDataSource

    @Singleton
    @Binds
    fun bindCurrentAdministrativeUnitDataSource(
        currentLocalAdministrativeUnitDataSource: CurrentLocalAdministrativeUnitDataSource
    ): CurrentAdministrativeUnitDataSource

    @Singleton
    @Binds
    fun bindCurrentRegionDataSource(
        currentRegionLocalDataSource: CurrentRegionLocalDataSource
    ): CurrentRegionDataSource
}