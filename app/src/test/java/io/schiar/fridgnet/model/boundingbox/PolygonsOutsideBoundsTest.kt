package io.schiar.fridgnet.model.boundingbox

import io.schiar.fridgnet.model.BoundingBox
import io.schiar.fridgnet.model.Coordinate
import org.junit.Assert
import org.junit.Test

class PolygonsOutsideBoundsTest {
    private val bounds = BoundingBox(
        southwest = Coordinate(latitude = 0.0, longitude = 0.0),
        northeast = Coordinate(latitude = 20.0, longitude = 20.0)
    )

    @Test
    fun `Polygon with southwest latitude equals bounds southwest latitude west of bounds`() {
        val polygon = BoundingBox(
            southwest = Coordinate(
                latitude = bounds.southwest.latitude,
                longitude = bounds.southwest.longitude - 10
            ),
            northeast = Coordinate(
                latitude = bounds.southwest.latitude + 5,
                longitude = bounds.southwest.longitude - 5
            )
        )

        Assert.assertFalse(bounds.contains(other = polygon))
    }

    @Test
    fun `Polygon with southwest latitude equals bounds southwest latitude east of bounds`() {
        val polygon = BoundingBox(
            southwest = Coordinate(
                latitude = bounds.southwest.latitude,
                longitude = bounds.northeast.longitude + 5
            ),
            northeast = Coordinate(
                latitude = bounds.southwest.latitude + 5,
                longitude = bounds.northeast.longitude + 10
            )
        )

        Assert.assertFalse(bounds.contains(other = polygon))
    }

    @Test
    fun `Polygon with southwest longitude equals bounds southwest longitude south of bounds`() {
        val polygon = BoundingBox(
            southwest = Coordinate(
                latitude = bounds.southwest.latitude - 10,
                longitude = bounds.southwest.longitude
            ),
            northeast = Coordinate(
                latitude = bounds.southwest.latitude - 5,
                longitude = bounds.southwest.longitude - 5
            )
        )

        Assert.assertFalse(bounds.contains(other = polygon))
    }

    @Test
    fun `Polygon with southwest longitude equals bounds southwest longitude north of bounds`() {
        val polygon = BoundingBox(
            southwest = Coordinate(
                latitude = bounds.northeast.latitude + 5,
                longitude = bounds.southwest.longitude
            ),
            northeast = Coordinate(
                latitude = bounds.northeast.latitude + 10,
                longitude = bounds.southwest.longitude + 5
            )
        )

        Assert.assertFalse(bounds.contains(other = polygon))
    }

    @Test
    fun `Polygon with southwest latitude equals bounds northeast latitude west of bounds`() {
        val polygon = BoundingBox(
            southwest = Coordinate(
                latitude = bounds.northeast.latitude,
                longitude = bounds.southwest.longitude - 10
            ),
            northeast = Coordinate(
                latitude = bounds.northeast.latitude + 5,
                longitude = bounds.southwest.longitude - 5
            )
        )

        Assert.assertFalse(bounds.contains(other = polygon))
    }

    @Test
    fun `Polygon with southwest latitude equals bounds northeast latitude east of bounds`() {
        val polygon = BoundingBox(
            southwest = Coordinate(
                latitude = bounds.northeast.latitude,
                longitude = bounds.northeast.longitude + 5
            ),
            northeast = Coordinate(
                latitude = bounds.northeast.latitude + 5,
                longitude = bounds.northeast.longitude + 10
            )
        )

        Assert.assertFalse(bounds.contains(other = polygon))
    }

    @Test
    fun `Polygon with southwest longitude equals bounds northeast longitude south of bounds`() {
        val polygon = BoundingBox(
            southwest = Coordinate(
                latitude = bounds.southwest.latitude - 10,
                longitude = bounds.northeast.longitude
            ),
            northeast = Coordinate(
                latitude = bounds.southwest.latitude - 5,
                longitude = bounds.northeast.longitude + 5
            )
        )

        Assert.assertFalse(bounds.contains(other = polygon))
    }

    @Test
    fun `Polygon with southwest longitude equals bounds northeast longitude north of bounds`() {
        val polygon = BoundingBox(
            southwest = Coordinate(
                latitude = bounds.northeast.latitude + 5,
                longitude = bounds.northeast.longitude
            ),
            northeast = Coordinate(
                latitude = bounds.northeast.latitude + 10,
                longitude = bounds.northeast.longitude + 5
            )
        )

        Assert.assertFalse(bounds.contains(other = polygon))
    }

    @Test
    fun `Polygon with northeast latitude equals bounds southwest latitude west of bounds`() {
        val polygon = BoundingBox(
            southwest = Coordinate(
                latitude = bounds.southwest.latitude - 5,
                longitude = bounds.southwest.longitude - 10
            ),
            northeast = Coordinate(
                latitude = bounds.southwest.latitude,
                longitude = bounds.southwest.longitude - 5
            )
        )

        Assert.assertFalse(bounds.contains(other = polygon))
    }

    @Test
    fun `Polygon with northeast latitude equals bounds southwest latitude east of bounds`() {
        val polygon = BoundingBox(
            southwest = Coordinate(
                latitude = bounds.northeast.latitude + 5,
                longitude = bounds.northeast.longitude + 5
            ),
            northeast = Coordinate(
                latitude = bounds.southwest.latitude,
                longitude = bounds.northeast.longitude + 10
            )
        )

        Assert.assertFalse(bounds.contains(other = polygon))
    }

    @Test
    fun `Polygon with northeast longitude equals bounds southwest longitude south of bounds`() {
        val polygon = BoundingBox(
            southwest = Coordinate(
                latitude = bounds.southwest.latitude - 10,
                longitude = bounds.southwest.longitude - 5
            ),
            northeast = Coordinate(
                latitude = bounds.southwest.latitude - 5,
                longitude = bounds.southwest.longitude
            )
        )

        Assert.assertFalse(bounds.contains(other = polygon))
    }

    @Test
    fun `Polygon with northeast longitude equals bounds southwest longitude north of bounds`() {
        val polygon = BoundingBox(
            southwest = Coordinate(
                latitude = bounds.northeast.latitude + 5,
                longitude = bounds.southwest.longitude - 5
            ),
            northeast = Coordinate(
                latitude = bounds.northeast.latitude + 10,
                longitude = bounds.southwest.longitude
            )
        )

        Assert.assertFalse(bounds.contains(other = polygon))
    }

    @Test
    fun `Polygon with northeast latitude equals bounds northeast latitude east of bounds`() {
        val polygon = BoundingBox(
            southwest = Coordinate(
                latitude = bounds.northeast.latitude - 5,
                longitude = bounds.northeast.longitude + 5
            ),
            northeast = Coordinate(
                latitude = bounds.northeast.latitude,
                longitude = bounds.northeast.longitude + 10
            )
        )

        Assert.assertFalse(bounds.contains(other = polygon))
    }

    @Test
    fun `Polygon with northeast latitude equals bounds northeast latitude west of bounds`() {
        val polygon = BoundingBox(
            southwest = Coordinate(
                latitude = bounds.northeast.latitude - 5,
                longitude = bounds.southwest.longitude - 10
            ),
            northeast = Coordinate(
                latitude = bounds.northeast.latitude,
                longitude = bounds.southwest.longitude - 5
            )
        )

        Assert.assertFalse(bounds.contains(other = polygon))
    }

    @Test
    fun `Polygon with northeast longitude equals bounds northeast longitude south of bounds`() {
        val polygon = BoundingBox(
            southwest = Coordinate(
                latitude = bounds.southwest.latitude - 10,
                longitude = bounds.northeast.longitude - 5
            ),
            northeast = Coordinate(
                latitude = bounds.southwest.latitude - 5,
                longitude = bounds.northeast.longitude
            )
        )

        Assert.assertFalse(bounds.contains(other = polygon))
    }

    @Test
    fun `Polygon with northeast longitude equals bounds northeast longitude north of bounds`() {
        val polygon = BoundingBox(
            southwest = Coordinate(
                latitude = bounds.northeast.latitude + 5,
                longitude = bounds.northeast.longitude - 5
            ),
            northeast = Coordinate(
                latitude = bounds.northeast.latitude + 10,
                longitude = bounds.northeast.longitude
            )
        )

        Assert.assertFalse(bounds.contains(other = polygon))
    }

    @Test
    fun `Polygon with southwest and northeast latitude equals bounds southwest and northeast latitude east of bounds`() {
        val polygon = BoundingBox(
            southwest = Coordinate(
                latitude = bounds.southwest.latitude,
                longitude = bounds.northeast.longitude + 5
            ),
            northeast = Coordinate(
                latitude = bounds.northeast.latitude,
                longitude = bounds.northeast.longitude + 10
            )
        )

        Assert.assertFalse(bounds.contains(other = polygon))
    }

    @Test
    fun `Polygon with southwest and northeast longitude equals bounds southwest and northeast longitude south of bounds`() {
        val polygon = BoundingBox(
            southwest = Coordinate(
                latitude = bounds.southwest.latitude - 10,
                longitude = bounds.southwest.longitude
            ),
            northeast = Coordinate(
                latitude = bounds.southwest.latitude - 5,
                longitude = bounds.northeast.longitude
            )
        )

        Assert.assertFalse(bounds.contains(other = polygon))
    }

    @Test
    fun `Polygon with southwest and northeast longitude equals bounds southwest and northeast longitude north of bounds`() {
        val polygon = BoundingBox(
            southwest = Coordinate(
                latitude = bounds.northeast.latitude + 5,
                longitude = bounds.southwest.longitude
            ),
            northeast = Coordinate(
                latitude = bounds.northeast.latitude + 10,
                longitude = bounds.northeast.longitude
            )
        )

        Assert.assertFalse(bounds.contains(other = polygon))
    }

    @Test
    fun `Polygon southwest of bounds`() {
        val polygon = BoundingBox(
            southwest = Coordinate(
                latitude = bounds.southwest.latitude - 10,
                longitude = bounds.southwest.longitude - 10
            ),
            northeast = Coordinate(
                latitude = bounds.southwest.latitude - 5,
                longitude = bounds.southwest.longitude - 5
            )
        )

        Assert.assertFalse(bounds.contains(other = polygon))
    }

    @Test
    fun `Polygon northeast of bounds`() {
        val polygon = BoundingBox(
            southwest = Coordinate(
                latitude = bounds.northeast.latitude + 5,
                longitude = bounds.northeast.longitude + 5
            ),
            northeast = Coordinate(
                latitude = bounds.northeast.latitude + 10,
                longitude = bounds.northeast.longitude + 10
            )
        )

        Assert.assertFalse(bounds.contains(other = polygon))
    }

    @Test
    fun `Polygon southeast of bounds`() {
        val polygon = BoundingBox(
            southwest = Coordinate(
                latitude = bounds.southwest.latitude + 10,
                longitude = bounds.northeast.longitude + 5
            ),
            northeast = Coordinate(
                latitude = bounds.southwest.latitude + 5,
                longitude = bounds.northeast.longitude + 10
            )
        )

        Assert.assertFalse(bounds.contains(other = polygon))
    }

    @Test
    fun `Polygon northwest of bounds`() {
        val polygon = BoundingBox(
            southwest = Coordinate(
                latitude = bounds.northeast.latitude + 5,
                longitude = bounds.southwest.longitude + 10
            ),
            northeast = Coordinate(
                latitude = bounds.northeast.latitude + 10,
                longitude = bounds.southwest.longitude + 5
            )
        )

        Assert.assertFalse(bounds.contains(other = polygon))
    }

    @Test
    fun `Polygon with southwest and northeast different from bounds west of bounds`() {
        val polygon = BoundingBox(
            southwest = Coordinate(
                latitude = bounds.southwest.latitude + 5,
                longitude = bounds.southwest.longitude - 10
            ),
            northeast = Coordinate(
                latitude = bounds.southwest.latitude + 10,
                longitude = bounds.southwest.longitude - 5
            )
        )

        Assert.assertFalse(bounds.contains(other = polygon))
    }

    @Test
    fun `Polygon with southwest and northeast different from bounds east of bounds`() {
        val polygon = BoundingBox(
            southwest = Coordinate(
                latitude = bounds.northeast.latitude + 5,
                longitude = bounds.northeast.longitude + 5
            ),
            northeast = Coordinate(
                latitude = bounds.northeast.latitude + 10,
                longitude = bounds.northeast.longitude + 10
            )
        )

        Assert.assertFalse(bounds.contains(other = polygon))
    }


    @Test
    fun `Polygon with southwest and northeast different from bounds south of bounds`() {
        val polygon = BoundingBox(
            southwest = Coordinate(
                latitude = bounds.southwest.latitude - 10,
                longitude = bounds.southwest.longitude + 5
            ),
            northeast = Coordinate(
                latitude = bounds.southwest.latitude - 5,
                longitude = bounds.southwest.longitude + 10
            )
        )

        Assert.assertFalse(bounds.contains(other = polygon))
    }

    @Test
    fun `Polygon with southwest and northeast different from bounds north of bounds`() {
        val polygon = BoundingBox(
            southwest = Coordinate(
                latitude = bounds.northeast.latitude + 5,
                longitude = bounds.northeast.longitude - 10
            ),
            northeast = Coordinate(
                latitude = bounds.northeast.latitude + 10,
                longitude = bounds.northeast.longitude - 5
            )
        )

        Assert.assertFalse(bounds.contains(other = polygon))
    }
}