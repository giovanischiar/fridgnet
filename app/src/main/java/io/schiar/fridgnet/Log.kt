package io.schiar.fridgnet

import android.util.Log as AndroidLog

object Log {
    fun d(tag: String, msg: String) {
        AndroidLog.d(tag, "|${Thread.currentThread().name}|$msg")
    }
}