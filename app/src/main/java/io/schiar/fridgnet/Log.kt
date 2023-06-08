package io.schiar.fridgnet

import android.util.Log as AndroidLog

object Log {
    var fromAndroid: Boolean = false
    fun d(tag: String, msg: String) {
        val content = "${Thread.currentThread().name}\t$msg"
        if (fromAndroid) AndroidLog.d(tag, content) else println("$tag\t$content")
    }
}