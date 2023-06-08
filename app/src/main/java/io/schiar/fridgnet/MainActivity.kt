package io.schiar.fridgnet

import android.location.Geocoder
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import io.schiar.fridgnet.model.repository.address.AddressGeocoderDataSource
import io.schiar.fridgnet.model.repository.address.AddressGeocoderRepository
import io.schiar.fridgnet.model.repository.address.AddressRepository
import io.schiar.fridgnet.model.repository.image.ImageAndroidDataSource
import io.schiar.fridgnet.model.repository.image.ImageRepository
import io.schiar.fridgnet.model.repository.image.ImageURIRepository
import io.schiar.fridgnet.model.repository.location.LocationAPIDBRepository
import io.schiar.fridgnet.model.repository.location.LocationRepository
import io.schiar.fridgnet.model.repository.location.datasource.room.LocationDatabase
import io.schiar.fridgnet.view.FridgeApp
import io.schiar.fridgnet.viewmodel.MainViewModel
import io.schiar.fridgnet.viewmodel.util.MainViewModelFactory
import java.util.*

class MainActivity: ComponentActivity() {
    override fun onCreate(saveBundleInstance: Bundle?) {
        super.onCreate(saveBundleInstance)
        Log.fromAndroid = true
        val viewModelProvider = ViewModelProvider(this, MainViewModelFactory(
                locationRepository = createLocationRepository(),
                addressRepository = createAddressRepository(),
                imageRepository = createImageRepository()
            )
        )
        val viewModel = viewModelProvider[MainViewModel::class.java]

        setContent {
            FridgeApp(viewModel = viewModel)
        }
    }

    private fun createLocationRepository(): LocationRepository {
        val locationDatabase = LocationDatabase.getDatabase(context = applicationContext)
        return LocationAPIDBRepository(locationDatabase = locationDatabase)
    }

    private fun createAddressRepository(): AddressRepository {
        val geocoder = Geocoder(applicationContext, Locale.US)
        return AddressGeocoderRepository(
            dataSource = AddressGeocoderDataSource(geocoder = geocoder)
        )
    }

    private fun createImageRepository(): ImageRepository {
        val contentResolver = applicationContext.contentResolver
        return ImageURIRepository(
            dataSource = ImageAndroidDataSource(contentResolver = contentResolver)
        )
    }
}