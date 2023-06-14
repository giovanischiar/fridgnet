package io.schiar.fridgnet

import android.location.Geocoder
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import io.schiar.fridgnet.model.datasource.room.FridgnetDatabase
import io.schiar.fridgnet.model.repository.MainRepository
import io.schiar.fridgnet.model.repository.address.AddressDBDataSource
import io.schiar.fridgnet.model.repository.address.AddressGeocoderDBRepository
import io.schiar.fridgnet.model.repository.address.AddressGeocoderDataSource
import io.schiar.fridgnet.model.repository.address.AddressRepository
import io.schiar.fridgnet.model.repository.image.ImageAndroidDBRepository
import io.schiar.fridgnet.model.repository.image.ImageAndroidDataSource
import io.schiar.fridgnet.model.repository.image.ImageDBDataSource
import io.schiar.fridgnet.model.repository.image.ImageRepository
import io.schiar.fridgnet.model.repository.location.LocationAPIDBRepository
import io.schiar.fridgnet.model.repository.location.LocationDBDataSource
import io.schiar.fridgnet.model.repository.location.LocationRepository
import io.schiar.fridgnet.view.AppScreen
import io.schiar.fridgnet.viewmodel.*
import io.schiar.fridgnet.viewmodel.util.ViewModelFactory
import java.util.*

class MainActivity: ComponentActivity() {
    override fun onCreate(saveBundleInstance: Bundle?) {
        super.onCreate(saveBundleInstance)
        Log.fromAndroid = true
        val viewModelProvider = ViewModelProvider(this, ViewModelFactory(
            repository = createRepository()
        ))
        val appViewModel = viewModelProvider[AppViewModel::class.java]
        val homeViewModel = viewModelProvider[HomeViewModel::class.java]
        val mapViewModel = viewModelProvider[MapViewModel::class.java]
        val polygonsViewModel = viewModelProvider[PolygonsViewModel::class.java]
        val photosViewModel = viewModelProvider[PhotosViewModel::class.java]

        setContent {
            AppScreen(
                appViewModel = appViewModel,
                homeViewModel = homeViewModel,
                mapViewModel = mapViewModel,
                polygonsViewModel = polygonsViewModel,
                photosViewModel = photosViewModel
            )
        }
    }

    private fun createRepository(): MainRepository {
        return MainRepository(
            locationRepository = createLocationRepository(),
            addressRepository = createAddressRepository(),
            imageRepository = createImageRepository()
        )
    }

    private fun createLocationRepository(): LocationRepository {
        val fridgnetDatabase = FridgnetDatabase.getDatabase(context = applicationContext)
        val locationDAO = fridgnetDatabase.locationDAO()
        return LocationAPIDBRepository(
            locationDBDataSource = LocationDBDataSource(locationDAO = locationDAO)
        )
    }

    private fun createAddressRepository(): AddressRepository {
        val geocoder = Geocoder(applicationContext, Locale.US)
        val fridgnetDatabase = FridgnetDatabase.getDatabase(context = applicationContext)
        val addressDAO = fridgnetDatabase.addressDAO()
        return AddressGeocoderDBRepository(
            addressGeocoderDataSource = AddressGeocoderDataSource(geocoder = geocoder),
            addressDBDataSource = AddressDBDataSource(addressDAO = addressDAO)
        )
    }

    private fun createImageRepository(): ImageRepository {
        val contentResolver = applicationContext.contentResolver
        val fridgnetDatabase = FridgnetDatabase.getDatabase(context = applicationContext)
        val imageDAO = fridgnetDatabase.imageDAO()
        return ImageAndroidDBRepository(
            imageAndroidDataSource = ImageAndroidDataSource(contentResolver = contentResolver),
            imageDBDataSource = ImageDBDataSource(imageDAO = imageDAO)
        )
    }
}