package io.schiar.fridgnet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import io.schiar.fridgnet.view.home.HomeScreen
import io.schiar.fridgnet.viewmodel.HomeViewModel

/**
 * The entry point of the application
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    /**
     * Overrides the first method call for the application. It sets the log to use the Android
     * logging system before call the first compose function.
     */
    override fun onCreate(saveBundleInstance: Bundle?) {
        super.onCreate(saveBundleInstance)
        Log.fromAndroid = true
        setContent {
            val homeViewModel = hiltViewModel<HomeViewModel>()
            HomeScreen(addURIs = homeViewModel::addURIs)
        }
    }
}