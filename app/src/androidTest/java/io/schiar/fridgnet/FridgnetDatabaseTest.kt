package io.schiar.fridgnet

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.schiar.fridgnet.library.room.AdministrativeUnitNameRoomDataSource
import io.schiar.fridgnet.library.room.CartographicBoundaryRoomDataSource
import io.schiar.fridgnet.library.room.FridgnetDatabase
import io.schiar.fridgnet.model.*
import io.schiar.fridgnet.model.datasource.AdministrativeUnitNameDataSource
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.equalTo
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class FridgnetDatabaseTest {
    private lateinit var database: FridgnetDatabase
    private lateinit var cartographicBoundaryRoomService: CartographicBoundaryRoomDataSource
    private lateinit var administrativeUnitNameDataSource: AdministrativeUnitNameDataSource

    @Before
    fun createDatabase() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context = context,
            klass = FridgnetDatabase::class.java
        ).build()
        cartographicBoundaryRoomService = CartographicBoundaryRoomDataSource(
            cartographicBoundaryDAO = database.cartographicBoundaryDAO()
        )
        administrativeUnitNameDataSource = AdministrativeUnitNameRoomDataSource(
            administrativeUnitNameDAO = database.administrativeUnitNameDAO()
        )
    }

    @After
    @Throws(IOException::class)
    fun closeDatabase() {
        database.close()
    }

    @Test
    fun insertCartographicBoundaryAndEnsureTheCartographicBoundaryRetrievedIsTheSame() = runTest {
        val administrativeUnitName = AdministrativeUnitName(
            locality = "Donut Square",
            subAdminArea = "Square County",
            adminArea = "Square State",
            countryName = "Square Country",
        )

        administrativeUnitNameDataSource.create(
            geoLocation = GeoLocation(latitude = 0.0, longitude = 0.0),
            administrativeUnitName
        )

        val (storedAdministrativeUnitNameDataSource) = administrativeUnitNameDataSource
            .retrieveAdministrativeUnitNameWithExistentCartographicBoundaries()
            .first()

        val cartographicBoundary = CartographicBoundary(
            administrativeUnitName = storedAdministrativeUnitNameDataSource,
            regions = listOf(
                Region(
                    polygon = Polygon(
                        geoLocations = listOf(
                            GeoLocation(latitude = -10, longitude = -10),
                            GeoLocation(latitude = -10, longitude = 10),
                            GeoLocation(latitude = 10, longitude = 10),
                            GeoLocation(latitude = 10, longitude = -10)
                        )
                    ),

                    holes = listOf(
                        Polygon(
                            geoLocations = listOf(
                                GeoLocation(latitude = -5, longitude = -5),
                                GeoLocation(latitude = -5, longitude = 5),
                                GeoLocation(latitude = 5, longitude = 5),
                                GeoLocation(latitude = 5, longitude = -5)
                            )
                        )
                    ),

                    active = true,

                    boundingBox = BoundingBox(
                        southwest = GeoLocation(latitude = -10, longitude = -10),
                        northeast = GeoLocation(latitude = 10, longitude = 10)
                    ),

                    zIndex = 1.0f
                )
            ),
            boundingBox = BoundingBox(
                southwest = GeoLocation(latitude = -10, longitude = -10),
                northeast = GeoLocation(latitude = 10, longitude = 10)
            ),
            zIndex = 1.0f,
            administrativeLevel = AdministrativeLevel.CITY
        )

        cartographicBoundaryRoomService.create(cartographicBoundary = cartographicBoundary)

        val actual = cartographicBoundaryRoomService
            .selectCartographicBoundaryByAdministrativeUnitName(
                administrativeUnitName = storedAdministrativeUnitNameDataSource
            )
            .first()

        assertThat(actual, equalTo(
            cartographicBoundary.copy(
                id = actual?.id ?: 0,
                regions = cartographicBoundary.regions.mapIndexed { index, region ->
                    val regionID = actual?.regions?.getOrNull(index)?.id ?: 0
                    val polygonID = actual?.regions?.getOrNull(index)?.polygon?.id ?: 0
                    region.copy(
                        id = regionID,
                        polygon = region.polygon.copy(id = polygonID),
                        holes = region.holes.mapIndexed { holeIndex, hole ->
                            val holeID = actual
                                ?.regions
                                ?.getOrNull(index)
                                ?.holes
                                ?.getOrNull(holeIndex)
                                ?.id ?: 0
                            hole.copy(id = holeID)
                        }
                    )
                }
            )
        ))
    }
}