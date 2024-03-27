package io.schiar.fridgnet.library.hilt

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope

@Module
@InstallIn(SingletonComponent::class)
object CoroutineScopeProvider {
    @OptIn(DelicateCoroutinesApi::class)
    @Provides
    fun provideCoroutineScope(): CoroutineScope {
        return GlobalScope
    }
}