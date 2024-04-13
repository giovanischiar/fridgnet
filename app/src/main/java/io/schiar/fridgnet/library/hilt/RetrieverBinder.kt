package io.schiar.fridgnet.library.hilt

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.schiar.fridgnet.library.android.ImageAndroidRetriever
import io.schiar.fridgnet.library.geocoder.AdministrativeUnitNameGeocoderRetriever
import io.schiar.fridgnet.library.retrofit.CartographicBoundaryRetrofitRetriever
import io.schiar.fridgnet.model.datasource.retriever.AdministrativeUnitNameRetriever
import io.schiar.fridgnet.model.datasource.retriever.CartographicBoundaryRetriever
import io.schiar.fridgnet.model.datasource.retriever.ImageRetriever

/**
 * Tells Hilt which implementation should be used for injected retriever interfaces. This module
 * is installed in the SingletonComponent.
 */
@Module
@InstallIn(SingletonComponent::class)
interface RetrieverBinder {
    /**
     * Tells Hilt to use the [ImageAndroidRetriever] implementation when injecting the
     * [ImageRetriever] interface.
     *
     * @param imageAndroidRetriever the instance of [ImageAndroidRetriever] to be used.
     * @return the [ImageRetriever] interface bound to the provided implementation.
     */
    @Binds
    fun bindImageRetriever(imageAndroidRetriever: ImageAndroidRetriever): ImageRetriever

    /**
     * Tells Hilt to use the [AdministrativeUnitNameGeocoderRetriever] implementation when injecting
     * the [AdministrativeUnitNameRetriever] interface.
     *
     * @param administrativeUnitNameGeocoderRetriever the instance of
     * [AdministrativeUnitNameGeocoderRetriever] to be used.
     * @return the [AdministrativeUnitNameRetriever] interface bound to the provided implementation.
     */
    @Binds
    fun bindAdministrativeUnitNameRetriever(
        administrativeUnitNameGeocoderRetriever: AdministrativeUnitNameGeocoderRetriever
    ): AdministrativeUnitNameRetriever

    /**
     * Tells Hilt to use the [CartographicBoundaryRetrofitRetriever] implementation when injecting
     * the [CartographicBoundaryRetriever] interface.
     *
     * @param cartographicBoundaryRetrofitRetriever the instance of
     * [CartographicBoundaryRetrofitRetriever] to be used.
     * @return the [CartographicBoundaryRetriever] interface bound to the provided implementation.
     */
    @Binds
    fun bindCartographicBoundaryRetriever(
        cartographicBoundaryRetrofitRetriever: CartographicBoundaryRetrofitRetriever
    ): CartographicBoundaryRetriever
}