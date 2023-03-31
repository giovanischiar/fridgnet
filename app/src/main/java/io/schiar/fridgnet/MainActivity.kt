package io.schiar.fridgnet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import io.schiar.fridgnet.view.FridgeApp

class MainActivity: ComponentActivity() {
    override fun onCreate(saveBundleInstance: Bundle?) {
        super.onCreate(saveBundleInstance)
        setContent {
            FridgeApp()
        }
    }
}