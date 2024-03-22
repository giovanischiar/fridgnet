package io.schiar.fridgnet

import android.location.Geocoder
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import io.schiar.fridgnet.library.android.ImageAndroidRetriever
import io.schiar.fridgnet.library.geocoder.AdministrativeUnitNameGeocoderRetriever
import io.schiar.fridgnet.library.retrofit.CartographicBoundaryRetrofitRetriever
import io.schiar.fridgnet.library.retrofit.NominatimAPI
import io.schiar.fridgnet.library.retrofit.RetrofitHelper
import io.schiar.fridgnet.library.room.AdministrativeUnitNameRoomDataSource
import io.schiar.fridgnet.library.room.CartographicBoundaryRoomDataSource
import io.schiar.fridgnet.library.room.FridgnetDatabase
import io.schiar.fridgnet.library.room.ImageRoomDataSource
import io.schiar.fridgnet.model.datasource.AdministrativeUnitNameDataSource
import io.schiar.fridgnet.model.datasource.CartographicBoundaryDataSource
import io.schiar.fridgnet.model.datasource.local.CurrentLocalAdministrativeUnitDataSource
import io.schiar.fridgnet.model.datasource.local.CurrentRegionLocalDataSource
import io.schiar.fridgnet.model.datasource.retriever.AdministrativeUnitNameRetriever
import io.schiar.fridgnet.model.datasource.retriever.CartographicBoundaryRetriever
import io.schiar.fridgnet.model.datasource.retriever.ImageRetriever
import io.schiar.fridgnet.model.datasource.ImageDataSource
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
        val administrativeUnitNameService = createAdministrativeUnitNameDataSource()
        val cartographicBoundaryDataSource = createCartographicBoundaryDataSource()
        val currentRegionDataSource = CurrentRegionLocalDataSource()
        val currentAdministrativeUnitDataSource
            = CurrentLocalAdministrativeUnitDataSource()

        val polygonsRepository = PolygonsRepository(
            currentRegionDataSource = currentRegionDataSource,
            cartographicBoundaryDataSource = cartographicBoundaryDataSource
        )
        val mapRepository = MapRepository(
            cartographicBoundaryDataSource = cartographicBoundaryDataSource,
            imageDataSource = imageDataSource,
            currentRegionDataSource = currentRegionDataSource
        )

        val photosRepository = PhotosRepository(
            currentAdministrativeUnitDataSource = currentAdministrativeUnitDataSource,
            imageDataSource = imageDataSource,
            administrativeUnitNameService = administrativeUnitNameService
        )

        val homeRepository = HomeRepository(
            administrativeUnitNameRetriever = createAdministrativeUnitNameRetriever(),
            administrativeUnitNameDataSource = administrativeUnitNameService,
            cartographicBoundaryRetriever = createCartographicBoundaryRetriever(),
            cartographicBoundaryDataSource = cartographicBoundaryDataSource,
            imageDataSource = imageDataSource,
            currentAdministrativeUnitDataSource = currentAdministrativeUnitDataSource,
            externalScope = GlobalScope
        )

        val appRepository = AppRepository(
            imageRetriever = createImageRetriever(),
            imageDataSource = imageDataSource
        )

        return Repositories(
            appRepository, homeRepository, mapRepository, polygonsRepository, photosRepository
        )
    }

    private fun createImageRetriever(): ImageRetriever {
        val contentResolver = applicationContext.contentResolver
        return ImageAndroidRetriever(contentResolver = contentResolver)
    }

    private fun createImageDataSource(): ImageDataSource {
        val fridgnetDatabase = FridgnetDatabase.getDatabase(context = applicationContext)
        val imageDAO = fridgnetDatabase.imageDAO()
        return ImageRoomDataSource(imageDAO = imageDAO)
    }

    private fun createAdministrativeUnitNameRetriever(): AdministrativeUnitNameRetriever {
        val geocoder = Geocoder(applicationContext, Locale.US)
        return AdministrativeUnitNameGeocoderRetriever(geocoder = geocoder)
    }

    private fun createAdministrativeUnitNameDataSource(): AdministrativeUnitNameDataSource {
        val fridgnetDatabase = FridgnetDatabase.getDatabase(context = applicationContext)
        val administrativeUnitNameDAO = fridgnetDatabase.administrativeUnitNameDAO()
        return AdministrativeUnitNameRoomDataSource(
            administrativeUnitNameDAO = administrativeUnitNameDAO
        )
    }

    private fun createCartographicBoundaryRetriever(): CartographicBoundaryRetriever {
        val retrofitHelper = RetrofitHelper.getInstance()
        val nominatimAPI = retrofitHelper.create(NominatimAPI::class.java)
        return CartographicBoundaryRetrofitRetriever(nominatimAPI = nominatimAPI)
    }

    private fun createCartographicBoundaryDataSource(): CartographicBoundaryDataSource {
        val fridgnetDatabase = FridgnetDatabase.getDatabase(context = applicationContext)
        val cartographicBoundaryDAO = fridgnetDatabase.cartographicBoundaryDAO()
        return CartographicBoundaryRoomDataSource(
            cartographicBoundaryDAO = cartographicBoundaryDAO
        )
    }
}