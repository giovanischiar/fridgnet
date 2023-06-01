package io.schiar.fridgnet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import io.schiar.fridgnet.model.repository.LocationAPIDBRepository
import io.schiar.fridgnet.model.repository.datasource.room.LocationDatabase
import io.schiar.fridgnet.view.FridgeApp
import io.schiar.fridgnet.viewmodel.MainViewModel
import io.schiar.fridgnet.viewmodel.util.MainViewModelFactory

class MainActivity: ComponentActivity() {
    override fun onCreate(saveBundleInstance: Bundle?) {
        super.onCreate(saveBundleInstance)
        val locationDatabase = LocationDatabase.getDatabase(context = applicationContext)
        val viewModelProvider = ViewModelProvider(this, MainViewModelFactory(
            LocationAPIDBRepository(locationDatabase = locationDatabase)
        ))
        val viewModel = viewModelProvider[MainViewModel::class.java]

        setContent {
            FridgeApp(viewModel = viewModel)
        }
    }
}