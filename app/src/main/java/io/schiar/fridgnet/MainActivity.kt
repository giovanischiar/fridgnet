package io.schiar.fridgnet

import android.location.Geocoder
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import io.schiar.fridgnet.model.repository.MainRepository
import io.schiar.fridgnet.model.repository.Repository
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
import io.schiar.fridgnet.viewmodel.HomeViewModel
import io.schiar.fridgnet.viewmodel.MainViewModel
import io.schiar.fridgnet.viewmodel.MapViewModel
import io.schiar.fridgnet.viewmodel.PolygonsViewModel
import io.schiar.fridgnet.viewmodel.util.ViewModelFactory
import java.util.*

class MainActivity: ComponentActivity() {
    override fun onCreate(saveBundleInstance: Bundle?) {
        super.onCreate(saveBundleInstance)
        Log.fromAndroid = true
        val viewModelProvider = ViewModelProvider(this, ViewModelFactory(
            repository = createRepository()
        ))
        val mainViewModel = viewModelProvider[MainViewModel::class.java]
        val homeViewModel = viewModelProvider[HomeViewModel::class.java]
        val mapViewModel = viewModelProvider[MapViewModel::class.java]
        val polygonsViewModel = viewModelProvider[PolygonsViewModel::class.java]

        setContent {
            FridgeApp(
                mainViewModel = mainViewModel,
                homeViewModel = homeViewModel,
                mapViewModel = mapViewModel,
                polygonsViewModel = polygonsViewModel
            )
        }
    }

    private fun createRepository(): Repository {
        return MainRepository(
            locationRepository = createLocationRepository(),
            addressRepository = createAddressRepository(),
            imageRepository = createImageRepository()
        )
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