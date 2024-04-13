package io.schiar.fridgnet.library.hilt

import android.content.ContentResolver
import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

/**
 * Provides the content resolver used by the Image Android Retriever to access image data.
 * This module is installed in the SingletonComponent.
 *
 * The content resolver allows the Image Android Retriever to interact with the device's content
 * providers, such as the MediaStore, to retrieve image information or load image data.
 */
@Module
@InstallIn(SingletonComponent::class)
object ContentResolverProvider {
    /**
     * Provides the content resolver using the application context.
     *
     * @param context the ApplicationContext provided by Hilt from Android
     * @return the [ContentResolver] instance
     */
    @Provides
    fun provideContentResolver(@ApplicationContext context: Context): ContentResolver {
        return context.contentResolver
    }
}