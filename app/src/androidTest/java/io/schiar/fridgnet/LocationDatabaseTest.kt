package io.schiar.fridgnet

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.schiar.fridgnet.model.*
import io.schiar.fridgnet.model.repository.datasource.LocationDBDataSource
import io.schiar.fridgnet.model.repository.datasource.room.LocationDatabase
import org.hamcrest.CoreMatchers.equalTo
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class LocationDatabaseTest {
    private lateinit var database: LocationDatabase
    private lateinit var locationDBDataSource: LocationDBDataSource

    @Before
    fun createDatabase() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context = context,
            klass = LocationDatabase::class.java
        ).build()
        locationDBDataSource = LocationDBDataSource(locationDatabase = database)
    }

    @After
    @Throws(IOException::class)
    fun closeDatabase() {
        database.close()
    }

    @Test
    fun insertLocationAndEnsureTheLocationRetrievedIsTheSame() {
        val region = Region(
            polygon = Polygon(
                coordinates = listOf(
                    Coordinate(latitude = -10, longitude = -10),
                    Coordinate(latitude = -10, longitude = 10),
                    Coordinate(latitude = 10, longitude = 10),
                    Coordinate(latitude = 10, longitude = -10)
                )
            ),

            holes = listOf(
                Polygon(
                    coordinates = listOf(
                        Coordinate(latitude = -5, longitude = -5),
                        Coordinate(latitude = -5, longitude = 5),
                        Coordinate(latitude = 5, longitude = 5),
                        Coordinate(latitude = 5, longitude = -5)
                    )
                )
            ),

            active = true,

            boundingBox = BoundingBox(
                southwest = Coordinate(latitude = -10, longitude = -10),
                northeast = Coordinate(latitude = 10, longitude = 10)
            ),
            zIndex = 1.0f
        )

        val address = Address(
            locality = "Donut Square",
            subAdminArea = "Square County",
            adminArea = "Square State",
            countryName = "Square Country"
        )

        val location = Location(
            address = address,
            regions = listOf(region),
            administrativeUnit = AdministrativeUnit.CITY,
            boundingBox = BoundingBox(
                southwest = Coordinate(latitude = -10, longitude = -10),
                northeast = Coordinate(latitude = 10, longitude = 10)
            ),
            zIndex = 1.0f
        )

        locationDBDataSource.insert(location = location)
        val actual = locationDBDataSource.selectLocationByAddress(address = address)
        assertThat(actual, equalTo(location))
    }
}