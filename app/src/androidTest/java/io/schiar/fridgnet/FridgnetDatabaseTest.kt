package io.schiar.fridgnet

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.schiar.fridgnet.library.room.CartographicBoundaryRoomService
import io.schiar.fridgnet.library.room.FridgnetDatabase
import io.schiar.fridgnet.model.*
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
    private lateinit var cartographicBoundaryRoomService: CartographicBoundaryRoomService

    @Before
    fun createDatabase() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context = context,
            klass = FridgnetDatabase::class.java
        ).build()
        cartographicBoundaryRoomService = CartographicBoundaryRoomService(cartographicBoundaryDAO = database.cartographicBoundaryDAO())
    }

    @After
    @Throws(IOException::class)
    fun closeDatabase() {
        database.close()
    }

    @Test
    fun insertCartographicBoundaryAndEnsureTheCartographicBoundaryRetrievedIsTheSame() = runTest {
        val region = Region(
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

        val administrativeUnit = AdministrativeUnit(
            locality = "Donut Square",
            subAdminArea = "Square County",
            adminArea = "Square State",
            countryName = "Square Country",
        )

        val cartographicBoundary = CartographicBoundary(
            administrativeUnit = administrativeUnit,
            regions = listOf(region),
            boundingBox = BoundingBox(
                southwest = GeoLocation(latitude = -10, longitude = -10),
                northeast = GeoLocation(latitude = 10, longitude = 10)
            ),
            zIndex = 1.0f,
            administrativeLevel = AdministrativeLevel.CITY
        )

        cartographicBoundaryRoomService.create(cartographicBoundary = cartographicBoundary)
        val actual = cartographicBoundaryRoomService.selectCartographicBoundaryByAdministrativeUnit(administrativeUnit = administrativeUnit).first()
        assertThat(actual, equalTo(cartographicBoundary))
    }
}