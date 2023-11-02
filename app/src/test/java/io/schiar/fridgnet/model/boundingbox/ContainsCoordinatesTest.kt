package io.schiar.fridgnet.model.boundingbox

import io.schiar.fridgnet.model.BoundingBox
import io.schiar.fridgnet.model.Coordinate
import org.junit.Assert
import org.junit.Test

class ContainsCoordinatesTest {
    private val bounds = BoundingBox(
        southwest = Coordinate(latitude = 0.0, longitude = 0.0),
        northeast = Coordinate(latitude = 10.0, longitude = 10.0)
    )

    private val boundsAntimeridian = BoundingBox(
        southwest = Coordinate(latitude = 0.0, longitude = 170.0),
        northeast = Coordinate(latitude = 10.0, longitude = -150.0)
    )

    @Test
    fun `latitude inside south of bounds`() {
        val latitude = bounds.southwest.latitude + 5

        Assert.assertTrue(bounds.containsLatitude(latitude = latitude))
    }

    @Test
    fun `latitude outside south of bounds`() {
        val latitude = bounds.southwest.latitude - 5

        Assert.assertFalse(bounds.containsLatitude(latitude = latitude))
    }

    @Test
    fun `latitude inside north of bounds`() {
        val latitude = bounds.northeast.latitude - 5

        Assert.assertTrue(bounds.containsLatitude(latitude = latitude))
    }

    @Test
    fun `longitude inside west of bounds`() {
        val longitude = bounds.southwest.longitude + 5

        Assert.assertTrue(bounds.containsLongitude(longitude = longitude))
    }

    @Test
    fun `longitude inside east of bounds`() {
        val longitude = bounds.northeast.longitude - 5

        Assert.assertTrue(bounds.containsLongitude(longitude = longitude))
    }

    @Test
    fun `longitude antimeridian outside of bounds`() {
        val longitude = -180.0

        Assert.assertFalse(bounds.containsLongitude(longitude = longitude))
    }

    @Test
    fun `latitude outside east of bounds`() {
        val latitude = bounds.northeast.latitude + 5

        Assert.assertFalse(bounds.containsLatitude(latitude = latitude))
    }

    @Test
    fun `longitude outside west of bounds`() {
        val longitude = bounds.southwest.longitude - 5

        Assert.assertFalse(bounds.containsLongitude(longitude = longitude))
    }

    @Test
    fun `longitude outside east of bounds`() {
        val longitude = bounds.northeast.longitude + 5

        Assert.assertFalse(bounds.containsLongitude(longitude = longitude))
    }

    @Test
    fun `longitude antimeridian inside of bounds with antimeridian`() {
        val longitude = -180.0

        Assert.assertTrue(boundsAntimeridian.containsLongitude(longitude = longitude))
    }

    @Test
    fun `longitude inside west of bounds with antimeridian`() {
        val longitude = boundsAntimeridian.southwest.longitude + 5

        Assert.assertTrue(boundsAntimeridian.containsLongitude(longitude = longitude))
    }

    @Test
    fun `longitude inside east of bounds with antimeridian`() {
        val longitude = boundsAntimeridian.northeast.longitude - 5

        Assert.assertTrue(boundsAntimeridian.containsLongitude(longitude = longitude))
    }

    @Test
    fun `longitude outside east of bounds with antimeridian`() {
        val longitude = boundsAntimeridian.northeast.longitude + 5

        Assert.assertFalse(boundsAntimeridian.containsLongitude(longitude = longitude))
    }

    @Test
    fun `longitude outside west of bounds with antimeridian`() {
        val longitude = boundsAntimeridian.southwest.longitude - 5

        Assert.assertFalse(boundsAntimeridian.containsLongitude(longitude = longitude))
    }
}