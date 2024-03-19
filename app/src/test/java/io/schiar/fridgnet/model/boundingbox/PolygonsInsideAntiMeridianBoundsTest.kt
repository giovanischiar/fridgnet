package io.schiar.fridgnet.model.boundingbox

import io.schiar.fridgnet.model.BoundingBox
import io.schiar.fridgnet.model.GeoLocation
import org.junit.Assert
import org.junit.Test

class PolygonsInsideAntiMeridianBoundsTest {
    private val bounds = BoundingBox(
        southwest = GeoLocation(latitude = 0.0, longitude = 170.0),
        northeast = GeoLocation(latitude = 10.0, longitude = -170.0)
    )

    @Test
    fun `polygon with southwest inside bounds`() {
        val polygon = BoundingBox(
            southwest = GeoLocation(
                latitude = bounds.northeast.latitude - 5,
                longitude = bounds.northeast.longitude - 5
            ),
            northeast = GeoLocation(
                latitude = bounds.northeast.latitude + 5,
                longitude = bounds.northeast.longitude + 5
            )
        )

        Assert.assertTrue(bounds.contains(other = polygon))
    }

    @Test
    fun `polygon with northeast inside bounds`() {
        val polygon = BoundingBox(
            southwest = GeoLocation(
                latitude = bounds.southwest.latitude - 5,
                longitude = bounds.southwest.longitude - 5
            ),
            northeast = GeoLocation(
                latitude = bounds.southwest.latitude + 5,
                longitude = bounds.southwest.longitude + 5
            )
        )

        Assert.assertTrue(bounds.contains(other = polygon))
    }

    @Test
    fun `polygon with northwest inside bounds`() {
        val polygon = BoundingBox(
            southwest = GeoLocation(
                latitude = bounds.northeast.latitude - 5,
                longitude = bounds.southwest.longitude - 5
            ),
            northeast = GeoLocation(
                latitude = bounds.northeast.latitude + 5,
                longitude = bounds.southwest.longitude + 5
            )
        )

        Assert.assertTrue(bounds.contains(other = polygon))
    }

    @Test
    fun `polygon with southeast inside bounds`() {
        val polygon = BoundingBox(
            southwest = GeoLocation(
                latitude = bounds.southwest.latitude - 5,
                longitude = bounds.northeast.longitude - 5
            ),
            northeast = GeoLocation(
                latitude = bounds.southwest.latitude + 5,
                longitude = bounds.northeast.longitude + 5
            )
        )

        Assert.assertTrue(bounds.contains(other = polygon))
    }

    @Test
    fun `polygon wraps only all north geo locations`() {
        val polygon = BoundingBox(
            southwest = GeoLocation(
                latitude = bounds.northeast.latitude - 5,
                longitude = bounds.southwest.longitude - 5
            ),
            northeast = GeoLocation(
                latitude = bounds.northeast.latitude + 5,
                longitude = bounds.northeast.longitude + 5
            )
        )

        Assert.assertTrue(bounds.contains(other = polygon))
    }

    @Test
    fun `polygon wraps only all east geo locations`() {
        val polygon = BoundingBox(
            southwest = GeoLocation(
                latitude = bounds.southwest.latitude - 5,
                longitude = bounds.northeast.longitude - 5
            ),
            northeast = GeoLocation(
                latitude = bounds.northeast.latitude + 5,
                longitude = bounds.northeast.longitude + 5
            )
        )

        Assert.assertTrue(bounds.contains(other = polygon))
    }

    @Test
    fun `polygon wraps only all south geo locations`() {
        val polygon = BoundingBox(
            southwest = GeoLocation(
                latitude = bounds.southwest.latitude + 5,
                longitude = bounds.northeast.longitude + 5
            ),
            northeast = GeoLocation(
                latitude = bounds.southwest.latitude + 5,
                longitude = bounds.southwest.longitude + 5
            )
        )

        Assert.assertTrue(bounds.contains(other = polygon))
    }

    @Test
    fun `polygon wraps only all west geo locations`() {
        val polygon = BoundingBox(
            southwest = GeoLocation(
                latitude = bounds.southwest.latitude - 5,
                longitude = bounds.southwest.longitude - 5
            ),
            northeast = GeoLocation(
                latitude = bounds.northeast.latitude + 5,
                longitude = bounds.southwest.longitude + 5
            )
        )

        Assert.assertTrue(bounds.contains(other = polygon))
    }

    @Test
    fun `polygon crosses the bounds north and south`() {
        val polygon = BoundingBox(
            southwest = GeoLocation(
                latitude = bounds.southwest.latitude - 5,
                longitude = bounds.southwest.longitude + 5
            ),
            northeast = GeoLocation(
                latitude = bounds.northeast.latitude + 5,
                longitude = bounds.northeast.longitude - 5
            )
        )

        Assert.assertTrue(bounds.contains(other = polygon))
    }

    @Test
    fun `polygon crosses the bounds west and east`() {
        val polygon = BoundingBox(
            southwest = GeoLocation(
                latitude = bounds.southwest.latitude + 5,
                longitude = bounds.southwest.longitude - 5
            ),
            northeast = GeoLocation(
                latitude = bounds.northeast.latitude + 5,
                longitude = bounds.northeast.longitude - 5
            )
        )

        Assert.assertTrue(bounds.contains(other = polygon))
    }
}