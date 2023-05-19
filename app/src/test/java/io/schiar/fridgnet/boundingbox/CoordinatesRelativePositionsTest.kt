package io.schiar.fridgnet.boundingbox

import io.schiar.fridgnet.model.BoundingBox
import io.schiar.fridgnet.model.Coordinate
import org.junit.Assert
import org.junit.Test

class CoordinatesRelativePositionsTest {
    @Test
    fun `West of Bounding box with antipode -180 check west`() {
        val boundingBox = BoundingBox(
            southwest = Coordinate(latitude = -10.0, longitude = -10.0),
            northeast = Coordinate(latitude = 10.0, longitude = 10.0)
        )

        val longitude = boundingBox.southwest.longitude - 5
        Assert.assertTrue(boundingBox.westOfLongitude(longitude = longitude))
    }

    @Test
    fun `West of Bounding box with antipode -180 check east`() {
        val boundingBox = BoundingBox(
            southwest = Coordinate(latitude = -10.0, longitude = -10.0),
            northeast = Coordinate(latitude = 10.0, longitude = 10.0)
        )

        val longitude = boundingBox.northeast.longitude + 5
        Assert.assertFalse(boundingBox.westOfLongitude(longitude = longitude))
    }

    @Test
    fun `East of Bounding box with antipode -180 check east`() {
        val boundingBox = BoundingBox(
            southwest = Coordinate(latitude = -10.0, longitude = -10.0),
            northeast = Coordinate(latitude = 10.0, longitude = 10.0)
        )

        val longitude = boundingBox.northeast.longitude + 5
        Assert.assertTrue(boundingBox.eastOfLongitude(longitude = longitude))
    }

    @Test
    fun `East of Bounding box with antipode -180 check west`() {
        val boundingBox = BoundingBox(
            southwest = Coordinate(latitude = -10.0, longitude = -10.0),
            northeast = Coordinate(latitude = 10.0, longitude = 10.0)
        )

        val longitude = boundingBox.southwest.longitude - 5
        Assert.assertFalse(boundingBox.eastOfLongitude(longitude = longitude))
    }

    @Test
    fun `West of Bounding box with antipode 0 check west`() {
        val boundingBox = BoundingBox(
            southwest = Coordinate(latitude = 0.0, longitude = 170.0),
            northeast = Coordinate(latitude = 10.0, longitude = -170.0)
        )

        val longitude = boundingBox.southwest.longitude - 5
        Assert.assertTrue(boundingBox.westOfLongitude(longitude = longitude))
    }

    @Test
    fun `West of Bounding box with antipode 0 check east`() {
        val boundingBox = BoundingBox(
            southwest = Coordinate(latitude = 0.0, longitude = 170.0),
            northeast = Coordinate(latitude = 10.0, longitude = -170.0)
        )

        val longitude = boundingBox.northeast.longitude + 5
        Assert.assertFalse(boundingBox.westOfLongitude(longitude = longitude))
    }

    @Test
    fun `East of Bounding box with antipode 0 check east`() {
        val boundingBox = BoundingBox(
            southwest = Coordinate(latitude = 0.0, longitude = 170.0),
            northeast = Coordinate(latitude = 10.0, longitude = -170.0)
        )

        val longitude = boundingBox.northeast.longitude + 5
        Assert.assertTrue(boundingBox.eastOfLongitude(longitude = longitude))
    }

    @Test
    fun `East of Bounding box with antipode 0 check west`() {
        val boundingBox = BoundingBox(
            southwest = Coordinate(latitude = 0.0, longitude = 170.0),
            northeast = Coordinate(latitude = 10.0, longitude = -170.0)
        )

        val longitude = boundingBox.southwest.longitude - 5
        Assert.assertFalse(boundingBox.eastOfLongitude(longitude = longitude))
    }

    @Test
    fun `West of Bounding box crossing the antimeridian with negative antipode check west`() {
        val boundingBox = BoundingBox(
            southwest = Coordinate(latitude = -10.0, longitude = 90.0),
            northeast = Coordinate(latitude = 10.0, longitude = -170.0)
        )

        val longitude = boundingBox.southwest.longitude - 5
        Assert.assertTrue(boundingBox.westOfLongitude(longitude = longitude))
    }

    @Test
    fun `West of Bounding box crossing the antimeridian with negative antipode check east`() {
        val boundingBox = BoundingBox(
            southwest = Coordinate(latitude = -10.0, longitude = 90.0),
            northeast = Coordinate(latitude = 10.0, longitude = -170.0)
        )

        val longitude = boundingBox.northeast.longitude + 5
        Assert.assertFalse(boundingBox.westOfLongitude(longitude = longitude))
    }

    @Test
    fun `East of Bounding box crossing the antimeridian with negative antipode check east`() {
        val boundingBox = BoundingBox(
            southwest = Coordinate(latitude = -10.0, longitude = 90.0),
            northeast = Coordinate(latitude = 10.0, longitude = -170.0)
        )

        val longitude = boundingBox.northeast.longitude + 5
        Assert.assertTrue(boundingBox.eastOfLongitude(longitude = longitude))
    }

    @Test
    fun `East of Bounding box crossing the antimeridian with negative antipode check west`() {
        val boundingBox = BoundingBox(
            southwest = Coordinate(latitude = -10.0, longitude = 90.0),
            northeast = Coordinate(latitude = 10.0, longitude = -170.0)
        )

        val longitude = boundingBox.southwest.longitude - 5
        Assert.assertFalse(boundingBox.eastOfLongitude(longitude = longitude))
    }

    @Test
    fun `West of Bounding box crossing the antimeridian with positive antipode check west`() {
        val boundingBox = BoundingBox(
            southwest = Coordinate(latitude = -10.0, longitude = 170.0),
            northeast = Coordinate(latitude = 10.0, longitude = -90.0)
        )

        val longitude = boundingBox.southwest.longitude - 5
        Assert.assertTrue(boundingBox.westOfLongitude(longitude = longitude))
    }

    @Test
    fun `West of Bounding box crossing the antimeridian with positive antipode check east`() {
        val boundingBox = BoundingBox(
            southwest = Coordinate(latitude = -10.0, longitude = 170.0),
            northeast = Coordinate(latitude = 10.0, longitude = -90.0)
        )

        val longitude = boundingBox.northeast.longitude + 5
        Assert.assertFalse(boundingBox.westOfLongitude(longitude = longitude))
    }

    @Test
    fun `East of Bounding box crossing the antimeridian with positive antipode check east`() {
        val boundingBox = BoundingBox(
            southwest = Coordinate(latitude = -10.0, longitude = 170.0),
            northeast = Coordinate(latitude = 10.0, longitude = -90.0)
        )

        val longitude = boundingBox.northeast.longitude + 5
        Assert.assertTrue(boundingBox.eastOfLongitude(longitude = longitude))
    }

    @Test
    fun `East of Bounding box crossing the antimeridian with positive antipode check west`() {
        val boundingBox = BoundingBox(
            southwest = Coordinate(latitude = -10.0, longitude = 170.0),
            northeast = Coordinate(latitude = 10.0, longitude = -90.0)
        )

        val longitude = boundingBox.southwest.longitude - 5
        Assert.assertFalse(boundingBox.eastOfLongitude(longitude = longitude))
    }

    @Test
    fun `West of Bounding box with negative antipode check west`() {
        val boundingBox = BoundingBox(
            southwest = Coordinate(latitude = -10.0, longitude = -10.0),
            northeast = Coordinate(latitude = 10.0, longitude = 90.0)
        )

        val longitude = boundingBox.southwest.longitude - 5
        Assert.assertTrue(boundingBox.westOfLongitude(longitude = longitude))
    }

    @Test
    fun `West of Bounding box with negative antipode check east`() {
        val boundingBox = BoundingBox(
            southwest = Coordinate(latitude = -10.0, longitude = -10.0),
            northeast = Coordinate(latitude = 10.0, longitude = 90.0)
        )

        val longitude = boundingBox.northeast.longitude + 5
        Assert.assertFalse(boundingBox.westOfLongitude(longitude = longitude))
    }

    @Test
    fun `East of Bounding box with negative antipode check east`() {
        val boundingBox = BoundingBox(
            southwest = Coordinate(latitude = -10.0, longitude = -10.0),
            northeast = Coordinate(latitude = 10.0, longitude = 90.0)
        )

        val longitude = boundingBox.northeast.longitude + 5
        Assert.assertTrue(boundingBox.eastOfLongitude(longitude = longitude))
    }

    @Test
    fun `East of Bounding box with negative antipode check west`() {
        val boundingBox = BoundingBox(
            southwest = Coordinate(latitude = -10.0, longitude = -10.0),
            northeast = Coordinate(latitude = 10.0, longitude = 90.0)
        )

        val longitude = boundingBox.southwest.longitude - 5
        Assert.assertFalse(boundingBox.eastOfLongitude(longitude = longitude))
    }

    @Test
    fun `West of Bounding box with positive antipode check west`() {
        val boundingBox = BoundingBox(
            southwest = Coordinate(latitude = -10.0, longitude = -90.0),
            northeast = Coordinate(latitude = 10.0, longitude = 10.0)
        )

        val longitude = boundingBox.southwest.longitude - 5
        Assert.assertTrue(boundingBox.westOfLongitude(longitude = longitude))
    }

    @Test
    fun `West of Bounding box with positive antipode check east`() {
        val boundingBox = BoundingBox(
            southwest = Coordinate(latitude = -10.0, longitude = -90.0),
            northeast = Coordinate(latitude = 10.0, longitude = 10.0)
        )

        val longitude = boundingBox.northeast.longitude + 5
        Assert.assertFalse(boundingBox.westOfLongitude(longitude = longitude))
    }

    @Test
    fun `East of Bounding box with positive antipode check east`() {
        val boundingBox = BoundingBox(
            southwest = Coordinate(latitude = -10.0, longitude = -90.0),
            northeast = Coordinate(latitude = 10.0, longitude = 10.0)
        )

        val longitude = boundingBox.northeast.longitude + 5
        Assert.assertTrue(boundingBox.eastOfLongitude(longitude = longitude))
    }

    @Test
    fun `East of Bounding box with positive antipode check west`() {
        val boundingBox = BoundingBox(
            southwest = Coordinate(latitude = -10.0, longitude = -90.0),
            northeast = Coordinate(latitude = 10.0, longitude = 10.0)
        )

        val longitude = boundingBox.southwest.longitude - 5
        Assert.assertFalse(boundingBox.eastOfLongitude(longitude = longitude))
    }

    @Test
    fun `South of Bounding box check south`() {
        val boundingBox = BoundingBox(
            southwest = Coordinate(latitude = -10.0, longitude = -10.0),
            northeast = Coordinate(latitude = 10.0, longitude = 10.0)
        )

        val latitude = boundingBox.southwest.latitude - 5
        Assert.assertTrue(boundingBox.southOfLatitude(latitude = latitude))
    }

    @Test
    fun `South of Bounding box check north`() {
        val boundingBox = BoundingBox(
            southwest = Coordinate(latitude = -10.0, longitude = -10.0),
            northeast = Coordinate(latitude = 10.0, longitude = 10.0)
        )

        val latitude = boundingBox.northeast.latitude + 5
        Assert.assertFalse(boundingBox.southOfLatitude(latitude = latitude))
    }

    @Test
    fun `North of Bounding box check north`() {
        val boundingBox = BoundingBox(
            southwest = Coordinate(latitude = -10.0, longitude = -10.0),
            northeast = Coordinate(latitude = 10.0, longitude = 10.0)
        )

        val latitude = boundingBox.northeast.latitude + 5
        Assert.assertTrue(boundingBox.northOfLatitude(latitude = latitude))
    }

    @Test
    fun `North of Bounding box check south`() {
        val boundingBox = BoundingBox(
            southwest = Coordinate(latitude = -10.0, longitude = -10.0),
            northeast = Coordinate(latitude = 10.0, longitude = 10.0)
        )

        val latitude = boundingBox.southwest.latitude - 5
        Assert.assertFalse(boundingBox.northOfLatitude(latitude = latitude))
    }
}