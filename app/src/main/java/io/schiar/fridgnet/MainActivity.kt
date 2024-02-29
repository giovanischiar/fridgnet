package io.schiar.fridgnet

import android.location.Geocoder
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import io.schiar.fridgnet.library.android.ImageAndroidRetriever
import io.schiar.fridgnet.library.geocoder.AddressGeocoderRetriever
import io.schiar.fridgnet.library.retrofit.LocationRetrofitRetriever
import io.schiar.fridgnet.library.retrofit.NominatimAPI
import io.schiar.fridgnet.library.retrofit.RetrofitHelper
import io.schiar.fridgnet.library.room.AddressRoomDataSource
import io.schiar.fridgnet.library.room.FridgnetDatabase
import io.schiar.fridgnet.library.room.ImageRoomDataSource
import io.schiar.fridgnet.library.room.LocationRoomDataSource
import io.schiar.fridgnet.model.repository.AppRepository
import io.schiar.fridgnet.model.repository.HomeRepository
import io.schiar.fridgnet.model.repository.MapRepository
import io.schiar.fridgnet.model.repository.PhotosRepository
import io.schiar.fridgnet.model.repository.PolygonsRepository
import io.schiar.fridgnet.model.repository.address.AddressGeocoderDBRepository
import io.schiar.fridgnet.model.repository.address.AddressRepository
import io.schiar.fridgnet.model.repository.image.ImageAndroidDBRepository
import io.schiar.fridgnet.model.repository.image.ImageRepository
import io.schiar.fridgnet.model.repository.location.LocationAPIDBRepository
import io.schiar.fridgnet.model.repository.location.LocationRepository
import io.schiar.fridgnet.view.screen.AppScreen
import io.schiar.fridgnet.viewmodel.AppViewModel
import io.schiar.fridgnet.viewmodel.HomeViewModel
import io.schiar.fridgnet.viewmodel.MapViewModel
import io.schiar.fridgnet.viewmodel.PhotosViewModel
import io.schiar.fridgnet.viewmodel.PolygonsViewModel
import io.schiar.fridgnet.viewmodel.util.ViewModelFactory
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(saveBundleInstance: Bundle?) {
        super.onCreate(saveBundleInstance)
        Log.fromAndroid = true
        val (
            appRepository, homeRepository, mapRepository, polygonsRepository, photosRepository
        ) = createRepositories()

        val viewModelProvider = ViewModelProvider(
            owner = this,
            ViewModelFactory(
                appRepository = appRepository,
                homeRepository = homeRepository,
                mapRepository = mapRepository,
                polygonsRepository = polygonsRepository,
                photosRepository = photosRepository
            )
        )
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

    data class Repositories(
        val appRepository: AppRepository,
        val homeRepository: HomeRepository,
        val mapRepository: MapRepository,
        val polygonsRepository: PolygonsRepository,
        val photosRepository: PhotosRepository
    )

    private fun createRepositories(): Repositories {
        val locationRepository = createLocationRepository()
        val addressRepository = createAddressRepository()
        val imageRepository = createImageRepository()

        val polygonsRepository = PolygonsRepository(
            locationRepository = locationRepository
        )
        val mapRepository = MapRepository(
            locationRepository = locationRepository,
            imageRepository = imageRepository
        )

        val photosRepository = PhotosRepository(
            imageRepository = imageRepository,
            locationRepository = locationRepository,
            addressRepository = addressRepository
        )

        val homeRepository = HomeRepository(
            addressRepository = addressRepository,
            locationRepository = locationRepository,
            imageRepository = imageRepository,
            onAddressReadyListener = photosRepository,
            onNewImageAddedListener = photosRepository
        )

        photosRepository.onLocationReadyListener = homeRepository

        val appRepository = AppRepository(
            locationRepository = locationRepository,
            addressRepository = addressRepository,
            imageRepository = imageRepository,
            onImageAddedListener = photosRepository
        )

        return Repositories(
            appRepository, homeRepository, mapRepository, polygonsRepository, photosRepository
        )
    }

    private fun createLocationRepository(): LocationRepository {
        val fridgnetDatabase = FridgnetDatabase.getDatabase(context = applicationContext)
        val locationDAO = fridgnetDatabase.locationDAO()
        val retrofitHelper = RetrofitHelper.getInstance()
        val nominatimAPI = retrofitHelper.create(NominatimAPI::class.java)
        return LocationAPIDBRepository(
            locationRetriever = LocationRetrofitRetriever(nominatimAPI = nominatimAPI),
            locationDataSource = LocationRoomDataSource(locationDAO = locationDAO)
        )
    }

    private fun createAddressRepository(): AddressRepository {
        val geocoder = Geocoder(applicationContext, Locale.US)
        val fridgnetDatabase = FridgnetDatabase.getDatabase(context = applicationContext)
        val addressDAO = fridgnetDatabase.addressDAO()
        return AddressGeocoderDBRepository(
            addressRetriever = AddressGeocoderRetriever(geocoder = geocoder),
            addressDataSource = AddressRoomDataSource(addressDAO = addressDAO)
        )
    }

    private fun createImageRepository(): ImageRepository {
        val contentResolver = applicationContext.contentResolver
        val fridgnetDatabase = FridgnetDatabase.getDatabase(context = applicationContext)
        val imageDAO = fridgnetDatabase.imageDAO()
        return ImageAndroidDBRepository(
            imageRetriever = ImageAndroidRetriever(contentResolver = contentResolver),
            imageDataSource = ImageRoomDataSource(imageDAO = imageDAO)
        )
    }
}