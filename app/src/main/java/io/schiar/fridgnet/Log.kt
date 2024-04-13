package io.schiar.fridgnet

import android.util.Log as AndroidLog

/**
 * Own implementation of Log to remove the Android use in the model during tests.
 */
object Log {
    /**
     * When set to false the model won't use the Android output
     */
    var fromAndroid: Boolean = false

    /**
     * The lookalike Log function from android that converts into println to use during tests that
     * don't have the android library.
     */
    fun d(tag: String, msg: String) {
        val content = "xxxFridgnetLogxxx ${Thread.currentThread().name}\t$msg"
        if (fromAndroid) AndroidLog.d(tag, content) else println("$tag\t$content")
    }
}