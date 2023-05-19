package io.schiar.fridgnet.boundingbox

import io.schiar.fridgnet.model.BoundingBox
import io.schiar.fridgnet.model.Coordinate
import org.junit.Assert
import org.junit.Test

class PolygonsInsideAntiMeridianBoundsTest {
    private val bounds = BoundingBox(
        southwest = Coordinate(latitude = 0.0,longitude = 170.0),
        northeast = Coordinate(latitude = 10.0, longitude = -170.0)
    )

    @Test
    fun `polygon with southwest inside bounds`() {
        val polygon = BoundingBox(
            southwest = Coordinate(
                latitude = bounds.northeast.latitude - 5,
                longitude = bounds.northeast.longitude - 5
            ),
            northeast = Coordinate(
                latitude = bounds.northeast.latitude + 5,
                longitude = bounds.northeast.longitude + 5
            )
        )

        Assert.assertTrue(bounds.contains(other = polygon))
    }

    @Test
    fun `polygon with northeast inside bounds`() {
        val polygon = BoundingBox(
            southwest = Coordinate(
                latitude = bounds.southwest.latitude - 5,
                longitude = bounds.southwest.longitude - 5
            ),
            northeast = Coordinate(
                latitude = bounds.southwest.latitude + 5,
                longitude = bounds.southwest.longitude + 5
            )
        )

        Assert.assertTrue(bounds.contains(other = polygon))
    }

    @Test
    fun `polygon with northwest inside bounds`() {
        val polygon = BoundingBox(
            southwest = Coordinate(
                latitude = bounds.northeast.latitude - 5,
                longitude = bounds.southwest.longitude - 5
            ),
            northeast = Coordinate(
                latitude = bounds.northeast.latitude + 5,
                longitude = bounds.southwest.longitude + 5
            )
        )

        Assert.assertTrue(bounds.contains(other = polygon))
    }

    @Test
    fun `polygon with southeast inside bounds`() {
        val polygon = BoundingBox(
            southwest = Coordinate(
                latitude = bounds.southwest.latitude - 5,
                longitude = bounds.northeast.longitude - 5
            ),
            northeast = Coordinate(
                latitude = bounds.southwest.latitude + 5,
                longitude = bounds.northeast.longitude + 5
            )
        )

        Assert.assertTrue(bounds.contains(other = polygon))
    }

    @Test
    fun `polygon wraps only all north coordinates`() {
        val polygon = BoundingBox(
            southwest = Coordinate(
                latitude = bounds.northeast.latitude - 5,
                longitude = bounds.southwest.longitude - 5
            ),
            northeast = Coordinate(
                latitude = bounds.northeast.latitude + 5,
                longitude = bounds.northeast.longitude + 5
            )
        )

        Assert.assertTrue(bounds.contains(other = polygon))
    }

    @Test
    fun `polygon wraps only all east coordinates`() {
        val polygon = BoundingBox(
            southwest = Coordinate(
                latitude = bounds.southwest.latitude - 5,
                longitude = bounds.northeast.longitude - 5
            ),
            northeast = Coordinate(
                latitude = bounds.northeast.latitude + 5,
                longitude = bounds.northeast.longitude + 5
            )
        )

        Assert.assertTrue(bounds.contains(other = polygon))
    }

    @Test
    fun `polygon wraps only all south coordinates`() {
        val polygon = BoundingBox(
            southwest = Coordinate(
                latitude = bounds.southwest.latitude + 5,
                longitude = bounds.northeast.longitude + 5
            ),
            northeast = Coordinate(
                latitude = bounds.southwest.latitude + 5,
                longitude = bounds.southwest.longitude + 5
            )
        )

        Assert.assertTrue(bounds.contains(other = polygon))
    }

    @Test
    fun `polygon wraps only all west coordinates`() {
        val polygon = BoundingBox(
            southwest = Coordinate(
                latitude = bounds.southwest.latitude - 5,
                longitude = bounds.southwest.longitude - 5
            ),
            northeast = Coordinate(
                latitude = bounds.northeast.latitude + 5,
                longitude = bounds.southwest.longitude + 5
            )
        )

        Assert.assertTrue(bounds.contains(other = polygon))
    }

    @Test
    fun `polygon crosses the bounds north and south`() {
        val polygon = BoundingBox(
            southwest = Coordinate(
                latitude = bounds.southwest.latitude - 5,
                longitude = bounds.southwest.longitude + 5
            ),
            northeast = Coordinate(
                latitude = bounds.northeast.latitude + 5,
                longitude = bounds.northeast.longitude - 5
            )
        )

        Assert.assertTrue(bounds.contains(other = polygon))
    }

    @Test
    fun `polygon crosses the bounds west and east`() {
        val polygon = BoundingBox(
            southwest = Coordinate(
                latitude = bounds.southwest.latitude + 5,
                longitude = bounds.southwest.longitude - 5
            ),
            northeast = Coordinate(
                latitude = bounds.northeast.latitude + 5,
                longitude = bounds.northeast.longitude - 5
            )
        )

        Assert.assertTrue(bounds.contains(other = polygon))
    }
}