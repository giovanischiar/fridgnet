package io.schiar.fridgnet.model

import org.junit.Assert
import org.junit.Test

class PolygonTest {
    @Test
    fun `Create a polygon with a list of negative coordinates`() {
        val polygon = Polygon(
            coordinates = listOf(
                Coordinate(latitude = -25, longitude = -25),
                Coordinate(latitude = -15, longitude = -30),
                Coordinate(latitude = -20, longitude = -40),
                Coordinate(latitude = -30, longitude = -45),
                Coordinate(latitude = -40, longitude = -35),
                Coordinate(latitude = -35, longitude = -20),
                Coordinate(latitude = -30, longitude = -15),
                Coordinate(latitude = -25, longitude = -25)
            )
        )

        val expected = BoundingBox(
            southwest = Coordinate(latitude = -40, longitude = -45),
            northeast = Coordinate(latitude = -15, longitude = -15)
        )

        Assert.assertEquals(expected, polygon.findBoundingBox())
    }

    @Test
    fun `Create a polygon with a list of positive coordinates`() {
        val polygon = Polygon(
            coordinates = listOf(
                Coordinate(latitude = 20, longitude = 20),
                Coordinate(latitude = 30, longitude = 40),
                Coordinate(latitude = 30, longitude = 40),
                Coordinate(latitude = 50, longitude = 30),
                Coordinate(latitude = 60, longitude = 20),
                Coordinate(latitude = 40, longitude = 10),
                Coordinate(latitude = 30, longitude = 10),
                Coordinate(latitude = 20, longitude = 20)
            )
        )

        val expected = BoundingBox(
            southwest = Coordinate(latitude = 20, longitude = 10),
            northeast = Coordinate(latitude = 60, longitude = 40)
        )

        Assert.assertEquals(expected, polygon.findBoundingBox())
    }

    @Test
    fun `Create a polygon with a list of positive latitude and negative longitude`() {
        val polygon = Polygon(
            coordinates = listOf(
                Coordinate(latitude = 40, longitude = -40),
                Coordinate(latitude = 60, longitude = -20),
                Coordinate(latitude = 80, longitude = -40),
                Coordinate(latitude = 100, longitude = -50),
                Coordinate(latitude = 80, longitude = -70),
                Coordinate(latitude = 50, longitude = -90),
                Coordinate(latitude = 50, longitude = -60),
                Coordinate(latitude = 40, longitude = -40)
            )
        )

        val expected = BoundingBox(
            southwest = Coordinate(latitude = 40, longitude = -90),
            northeast = Coordinate(latitude = 100, longitude = -20)
        )

        Assert.assertEquals(expected, polygon.findBoundingBox())
    }

    @Test
    fun `Create a polygon with a list of negative latitude and positive longitude`() {
        val polygon = Polygon(
            coordinates = listOf(
                Coordinate(latitude = -80, longitude = 100),
                Coordinate(latitude = -40, longitude = 120),
                Coordinate(latitude = -20, longitude = 80),
                Coordinate(latitude = -60, longitude = 80),
                Coordinate(latitude = -60, longitude = 40),
                Coordinate(latitude = -80, longitude = 20),
                Coordinate(latitude = -100, longitude = 60),
                Coordinate(latitude = -80, longitude = 100),
            )
        )

        val expected = BoundingBox(
            southwest = Coordinate(latitude = -100, longitude = 20),
            northeast = Coordinate(latitude = -20, longitude = 120)
        )

        Assert.assertEquals(expected, polygon.findBoundingBox())
    }

    @Test
    fun `Create a polygon crossing the antimeridian`() {
        val polygon = Polygon(
            coordinates = listOf(
                Coordinate(latitude = -20, longitude = 160),
                Coordinate(latitude = -60, longitude = 160),
                Coordinate(latitude = -40, longitude = -160),
                Coordinate(latitude = 0, longitude = -120),
                Coordinate(latitude = 20, longitude = -160),
                Coordinate(latitude = 60, longitude = 160),
                Coordinate(latitude = 20, longitude = 120),
                Coordinate(latitude = -20, longitude = 160),
            )
        )

        val expected = BoundingBox(
            southwest = Coordinate(latitude = -60, longitude = 120),
            northeast = Coordinate(latitude = 60, longitude = -120)
        )

        Assert.assertEquals(expected, polygon.findBoundingBox())
    }
}