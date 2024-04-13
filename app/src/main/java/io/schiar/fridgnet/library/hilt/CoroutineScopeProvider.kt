package io.schiar.fridgnet.library.hilt

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope

/**
 * Provides a coroutine scope to classes that need to launch coroutines. This module is installed
 * in the SingletonComponent.
 */
@Module
@InstallIn(SingletonComponent::class)
object CoroutineScopeProvider {
    /**
     * Provides the GlobalScope to coroutines that need to be executed outside the lifecycle of any
     * specific instance. Use with caution, as coroutines launched with GlobalScope can be difficult
     * to cancel and may lead to memory leaks if not properly handled. Consider using a more
     * specific coroutine scope whenever possible.
     *
     * @return the GlobalScope
     */
    @OptIn(DelicateCoroutinesApi::class)
    @Provides
    fun provideCoroutineScope(): CoroutineScope {
        return GlobalScope
    }
}