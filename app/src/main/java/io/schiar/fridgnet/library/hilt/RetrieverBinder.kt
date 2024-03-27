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

@Module
@InstallIn(SingletonComponent::class)
interface RetrieverBinder {
    @Binds
    fun bindImageRetriever(imageAndroidRetriever: ImageAndroidRetriever): ImageRetriever

    @Binds
    fun bindAdministrativeUnitNameRetriever(
        administrativeUnitNameGeocoderRetriever: AdministrativeUnitNameGeocoderRetriever
    ): AdministrativeUnitNameRetriever

    @Binds
    fun bindCartographicBoundaryRetriever(
        cartographicBoundaryRetrofitRetriever: CartographicBoundaryRetrofitRetriever
    ): CartographicBoundaryRetriever
}