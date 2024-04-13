package io.schiar.fridgnet.library.hilt

import android.content.Context
import android.location.Geocoder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.util.Locale

/**
 * Provides a [Geocoder] instance to the AdministrativeUnitNameGeocoder. This class uses the
 * [Geocoder] to retrieve the Address objects containing information for building AdministrativeUnit
 * objects. This module is installed in the SingletonComponent.
 *
 * By default, this function provides a [Geocoder] object using US as the locale. If your
 * application requires using a different locale for geocoding, consider injecting a custom Geocoder
 * instance instead of relying on this default provider.
 */
@Module
@InstallIn(SingletonComponent::class)
object GeocoderProvider {
    /**
     * Provides the Geocoder instance using the application context and US locale.
     *
     * @param context the ApplicationContext provided by Hilt from Android
     * @return the [Geocoder] object using US as the default Locale.
     */
    @Provides
    fun provideGeocoder(@ApplicationContext context: Context): Geocoder {
        return Geocoder(context, Locale.US)
    }
}