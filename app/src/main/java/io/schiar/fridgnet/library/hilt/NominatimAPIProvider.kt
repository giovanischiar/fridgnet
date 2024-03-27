package io.schiar.fridgnet.library.hilt

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.schiar.fridgnet.library.retrofit.NominatimAPI
import io.schiar.fridgnet.library.retrofit.RetrofitHelper

@Module
@InstallIn(SingletonComponent::class)
object NominatimAPIProvider {
    @Provides
    fun provideRetrofitAPI(): NominatimAPI {
        return RetrofitHelper.getInstance().create(NominatimAPI::class.java)
    }
}