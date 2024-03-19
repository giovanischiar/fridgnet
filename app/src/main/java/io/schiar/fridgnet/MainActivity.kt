package io.schiar.fridgnet

import android.location.Geocoder
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import io.schiar.fridgnet.library.android.ImageAndroidRetriever
import io.schiar.fridgnet.library.geocoder.AdministrativeUnitGeocoderRetriever
import io.schiar.fridgnet.library.retrofit.LocationRetrofitRetriever
import io.schiar.fridgnet.library.retrofit.NominatimAPI
import io.schiar.fridgnet.library.retrofit.RetrofitHelper
import io.schiar.fridgnet.library.room.AdministrativeUnitRoomService
import io.schiar.fridgnet.library.room.FridgnetDatabase
import io.schiar.fridgnet.library.room.ImageRoomService
import io.schiar.fridgnet.library.room.LocationRoomService
import io.schiar.fridgnet.model.datasource.AdministrativeUnitDataSource
import io.schiar.fridgnet.model.datasource.ImageDataSource
import io.schiar.fridgnet.model.datasource.LocationDataSource
import io.schiar.fridgnet.model.datasource.local.AdministrativeUnitGeoLocationsDataSource
import io.schiar.fridgnet.model.datasource.local.CurrentLocationGeoLocationDataSource
import io.schiar.fridgnet.model.datasource.local.CurrentRegionLocalDataSource
import io.schiar.fridgnet.model.datasource.local.ImageAndroidDBDataSource
import io.schiar.fridgnet.model.datasource.local.LocationAPIDBDataSource
import io.schiar.fridgnet.model.repository.AppRepository
import io.schiar.fridgnet.model.repository.HomeRepository
import io.schiar.fridgnet.model.repository.MapRepository
import io.schiar.fridgnet.model.repository.PhotosRepository
import io.schiar.fridgnet.model.repository.PolygonsRepository
import io.schiar.fridgnet.view.screen.AppScreen
import io.schiar.fridgnet.viewmodel.AppViewModel
import io.schiar.fridgnet.viewmodel.HomeViewModel
import io.schiar.fridgnet.viewmodel.MapViewModel
import io.schiar.fridgnet.viewmodel.PhotosViewModel
import io.schiar.fridgnet.viewmodel.PolygonsViewModel
import io.schiar.fridgnet.viewmodel.util.ViewModelFactory
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
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

    @OptIn(DelicateCoroutinesApi::class)
    private fun createRepositories(): Repositories {
        val imageDataSource = createImageDataSource()
        val administrativeUnitLocationsGeoLocationsDataSource = createAdministrativeUnitGeoLocationsDataSource()
        val locationDataSource = createLocationDataSource()
        val currentRegionDataSource = CurrentRegionLocalDataSource()
        val currentAdministrativeUnitLocationsGeoLocationsDataSource = CurrentLocationGeoLocationDataSource()

        val polygonsRepository = PolygonsRepository(
            currentRegionDataSource = currentRegionDataSource,
            locationDataSource = locationDataSource
        )
        val mapRepository = MapRepository(
            locationDataSource = locationDataSource,
            imageDataSource = imageDataSource,
            currentRegionDataSource = currentRegionDataSource
        )

        val photosRepository = PhotosRepository(
            currentAdministrativeUnitLocationsGeoLocationsDataSource
                = currentAdministrativeUnitLocationsGeoLocationsDataSource,
            imageDataSource = imageDataSource,
            administrativeUnitLocationsGeoLocationsDataSource = administrativeUnitLocationsGeoLocationsDataSource
        )

        val homeRepository = HomeRepository(
            administrativeUnitDataSource = administrativeUnitLocationsGeoLocationsDataSource,
            locationDataSource = locationDataSource,
            imageDataSource = imageDataSource,
            currentLocationGeoLocationDataSource = currentAdministrativeUnitLocationsGeoLocationsDataSource,
            externalScope = GlobalScope
        )

        val appRepository = AppRepository(imageDataSource = imageDataSource)

        return Repositories(
            appRepository, homeRepository, mapRepository, polygonsRepository, photosRepository
        )
    }

    private fun createImageDataSource(): ImageDataSource {
        val contentResolver = applicationContext.contentResolver
        val fridgnetDatabase = FridgnetDatabase.getDatabase(context = applicationContext)
        val imageDAO = fridgnetDatabase.imageDAO()
        return ImageAndroidDBDataSource(
            imageRetriever = ImageAndroidRetriever(contentResolver = contentResolver),
            imageService = ImageRoomService(imageDAO = imageDAO)
        )
    }

    private fun createAdministrativeUnitGeoLocationsDataSource(): AdministrativeUnitDataSource {
        val geocoder = Geocoder(applicationContext, Locale.US)
        val fridgnetDatabase = FridgnetDatabase.getDatabase(context = applicationContext)
        val administrativeUnitDAO = fridgnetDatabase.administrativeUnitDAO()
        return AdministrativeUnitGeoLocationsDataSource(
            administrativeUnitRetriever = AdministrativeUnitGeocoderRetriever(geocoder = geocoder),
            administrativeUnitService = AdministrativeUnitRoomService(administrativeUnitDAO = administrativeUnitDAO)
        )
    }

    private fun createLocationDataSource(): LocationDataSource {
        val fridgnetDatabase = FridgnetDatabase.getDatabase(context = applicationContext)
        val locationDAO = fridgnetDatabase.locationDAO()
        val retrofitHelper = RetrofitHelper.getInstance()
        val nominatimAPI = retrofitHelper.create(NominatimAPI::class.java)
        return LocationAPIDBDataSource(
            locationRetriever = LocationRetrofitRetriever(nominatimAPI = nominatimAPI),
            locationService = LocationRoomService(locationDAO = locationDAO)
        )
    }
}