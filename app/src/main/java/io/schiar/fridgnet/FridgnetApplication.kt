package io.schiar.fridgnet

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Subclass of [Application] to use the Hilt library
 */
@HiltAndroidApp
class FridgnetApplication: Application()