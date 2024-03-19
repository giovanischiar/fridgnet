package io.schiar.fridgnet.model

import org.junit.Assert
import org.junit.Test

class PolygonTest {
    @Test
    fun `Create a polygon with a list of negative geo locations`() {
        val polygon = Polygon(
            geoLocations = listOf(
                GeoLocation(latitude = -25, longitude = -25),
                GeoLocation(latitude = -15, longitude = -30),
                GeoLocation(latitude = -20, longitude = -40),
                GeoLocation(latitude = -30, longitude = -45),
                GeoLocation(latitude = -40, longitude = -35),
                GeoLocation(latitude = -35, longitude = -20),
                GeoLocation(latitude = -30, longitude = -15),
                GeoLocation(latitude = -25, longitude = -25)
            )
        )

        val expected = BoundingBox(
            southwest = GeoLocation(latitude = -40, longitude = -45),
            northeast = GeoLocation(latitude = -15, longitude = -15)
        )

        Assert.assertEquals(expected, polygon.findBoundingBox())
    }

    @Test
    fun `Create a polygon with a list of positive geo locations`() {
        val polygon = Polygon(
            geoLocations = listOf(
                GeoLocation(latitude = 20, longitude = 20),
                GeoLocation(latitude = 30, longitude = 40),
                GeoLocation(latitude = 30, longitude = 40),
                GeoLocation(latitude = 50, longitude = 30),
                GeoLocation(latitude = 60, longitude = 20),
                GeoLocation(latitude = 40, longitude = 10),
                GeoLocation(latitude = 30, longitude = 10),
                GeoLocation(latitude = 20, longitude = 20)
            )
        )

        val expected = BoundingBox(
            southwest = GeoLocation(latitude = 20, longitude = 10),
            northeast = GeoLocation(latitude = 60, longitude = 40)
        )

        Assert.assertEquals(expected, polygon.findBoundingBox())
    }

    @Test
    fun `Create a polygon with a list of positive latitude and negative longitude`() {
        val polygon = Polygon(
            geoLocations = listOf(
                GeoLocation(latitude = 40, longitude = -40),
                GeoLocation(latitude = 60, longitude = -20),
                GeoLocation(latitude = 80, longitude = -40),
                GeoLocation(latitude = 100, longitude = -50),
                GeoLocation(latitude = 80, longitude = -70),
                GeoLocation(latitude = 50, longitude = -90),
                GeoLocation(latitude = 50, longitude = -60),
                GeoLocation(latitude = 40, longitude = -40)
            )
        )

        val expected = BoundingBox(
            southwest = GeoLocation(latitude = 40, longitude = -90),
            northeast = GeoLocation(latitude = 100, longitude = -20)
        )

        Assert.assertEquals(expected, polygon.findBoundingBox())
    }

    @Test
    fun `Create a polygon with a list of negative latitude and positive longitude`() {
        val polygon = Polygon(
            geoLocations = listOf(
                GeoLocation(latitude = -80, longitude = 100),
                GeoLocation(latitude = -40, longitude = 120),
                GeoLocation(latitude = -20, longitude = 80),
                GeoLocation(latitude = -60, longitude = 80),
                GeoLocation(latitude = -60, longitude = 40),
                GeoLocation(latitude = -80, longitude = 20),
                GeoLocation(latitude = -100, longitude = 60),
                GeoLocation(latitude = -80, longitude = 100),
            )
        )

        val expected = BoundingBox(
            southwest = GeoLocation(latitude = -100, longitude = 20),
            northeast = GeoLocation(latitude = -20, longitude = 120)
        )

        Assert.assertEquals(expected, polygon.findBoundingBox())
    }

    @Test
    fun `Create a polygon crossing the antimeridian`() {
        val polygon = Polygon(
            geoLocations = listOf(
                GeoLocation(latitude = -20, longitude = 160),
                GeoLocation(latitude = -60, longitude = 160),
                GeoLocation(latitude = -40, longitude = -160),
                GeoLocation(latitude = 0, longitude = -120),
                GeoLocation(latitude = 20, longitude = -160),
                GeoLocation(latitude = 60, longitude = 160),
                GeoLocation(latitude = 20, longitude = 120),
                GeoLocation(latitude = -20, longitude = 160),
            )
        )

        val expected = BoundingBox(
            southwest = GeoLocation(latitude = -60, longitude = 120),
            northeast = GeoLocation(latitude = 60, longitude = -120)
        )

        Assert.assertEquals(expected, polygon.findBoundingBox())
    }
}