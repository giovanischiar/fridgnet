package io.schiar.fridgnet.library.hilt

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.schiar.fridgnet.library.retrofit.NominatimAPI
import io.schiar.fridgnet.library.retrofit.RetrofitHelper

/**
 * Provides a NominatimAPI object for interacting with the Nominatim API. This API is used to fetch
 * JSON data containing information necessary for building cartographic boundary objects. This
 * module is installed in the SingletonComponent.
 *
 * This provider relies on the [RetrofitHelper] module being installed and configured elsewhere in
 * the application to function correctly. The [RetrofitHelper] is likely responsible for setting up
 * the Retrofit client with the necessary configuration for interacting with the Nominatim API.
 */
@Module
@InstallIn(SingletonComponent::class)
object NominatimAPIProvider {
    /**
     * Uses the RetrofitHelper to create an instance of [NominatimAPI].
     *
     * @return the [NominatimAPI] object
     */
    @Provides
    fun provideRetrofitAPI(): NominatimAPI {
        return RetrofitHelper.getInstance().create(NominatimAPI::class.java)
    }
}