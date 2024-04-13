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
import io.schiar.fridgnet.model.datasource.CurrentRegionDataSource
import io.schiar.fridgnet.model.datasource.ImageDataSource
import io.schiar.fridgnet.model.datasource.local.AdministrativeUnitLocalDataSource
import io.schiar.fridgnet.model.datasource.local.CurrentRegionLocalDataSource
import javax.inject.Singleton

/**
 * Tells Hilt which implementation should be used for injected data source interfaces. This module
 * is installed in the SingletonComponent.
 */
@Module
@InstallIn(SingletonComponent::class)
interface DataSourceBinder {
    /**
     * Tells Hilt to use the [ImageRoomDataSource] implementation when injecting the
     * [ImageDataSource] interface.
     *
     * @param imageRoomDataSource the instance of [ImageRoomDataSource] to be used.
     * @return the [ImageDataSource] interface bound to the provided implementation.
     */
    @Binds
    fun bindImageDataSource(imageRoomDataSource: ImageRoomDataSource): ImageDataSource

    /**
     * Tells Hilt to use the [AdministrativeUnitNameRoomDataSource] implementation when injecting the
     * [AdministrativeUnitNameDataSource] interface.
     *
     * @param administrativeUnitNameRoomDataSource the instance of
     * [AdministrativeUnitNameRoomDataSource] to be used.
     * @return the [AdministrativeUnitNameDataSource] interface bound to the provided
     * implementation.
     */
    @Binds
    fun bindAdministrativeUnitNameDataSource(
        administrativeUnitNameRoomDataSource: AdministrativeUnitNameRoomDataSource
    ): AdministrativeUnitNameDataSource

    /**
     * Tells Hilt to use the [cartographicBoundaryRoomDataSource] when injecting the
     * [CartographicBoundaryDataSource].
     *
     * @param cartographicBoundaryRoomDataSource the instance of
     * [CartographicBoundaryRoomDataSource].
     * @return the [CartographicBoundaryDataSource].
     */
    @Binds
    fun bindCartographicBoundaryDataSource(
        cartographicBoundaryRoomDataSource: CartographicBoundaryRoomDataSource
    ): CartographicBoundaryDataSource

    /**
     * Tells Hilt to use a singleton instance of [AdministrativeUnitLocalDataSource] when injecting
     * the [AdministrativeUnitDataSource] interface. This ensures all classes have access to the
     * same instance.
     *
     * @param administrativeUnitLocalDataSource the singleton instance of
     * [AdministrativeUnitLocalDataSource] to be used.
     * @return the [AdministrativeUnitDataSource] interface bound to the provided singleton
     * implementation.
     */
    @Singleton
    @Binds
    fun bindAdministrativeUnitDataSource(
        administrativeUnitLocalDataSource: AdministrativeUnitLocalDataSource
    ): AdministrativeUnitDataSource

    /**
     * Tells Hilt to use a singleton instance of [CurrentRegionLocalDataSource] when injecting the
     * [CurrentRegionDataSource] interface. This ensures all classes have access to the same
     * instance.
     *
     * @param currentRegionLocalDataSource the singleton instance of
     * [CurrentRegionLocalDataSource] to be used.
     * @return the [CurrentRegionDataSource] interface bound to the provided singleton
     * implementation.
     */
    @Singleton
    @Binds
    fun bindCurrentRegionDataSource(
        currentRegionLocalDataSource: CurrentRegionLocalDataSource
    ): CurrentRegionDataSource
}