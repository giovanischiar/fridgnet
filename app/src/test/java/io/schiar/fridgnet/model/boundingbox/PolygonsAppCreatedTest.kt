package io.schiar.fridgnet.model.boundingbox

import io.schiar.fridgnet.model.BoundingBox
import io.schiar.fridgnet.model.Coordinate
import org.junit.Assert
import org.junit.Test

class PolygonsAppCreatedTest {
    private val boundingBox = BoundingBox(
        southwest = Coordinate(latitude = 16.89972761602733, longitude = 179.65680330991745),
        northeast = Coordinate(latitude = 34.25974311294054, longitude = -170.00527180731297)
    )

    @Test
    fun `Polygons app generated are inside bounding box 0`() {
        val polygon0 = BoundingBox(
            southwest = Coordinate(latitude = 32.5295236, longitude = -124.482003),
            northeast = Coordinate(latitude = 42.009499, longitude = -114.1307816)
        )

        Assert.assertFalse(boundingBox.contains(polygon0))
    }

    @Test
    fun `Polygons app generated are inside bounding box 1`() {
        val polygon1 = BoundingBox(
            southwest = Coordinate(latitude = 33.84158, longitude = -120.582264),
            northeast = Coordinate(latitude = 34.160569, longitude = -119.293112)
        )

        Assert.assertFalse(boundingBox.contains(polygon1))
    }

    @Test
    fun `Polygons app generated are inside bounding box 2`() {
        val polygon2 = BoundingBox(
            southwest = Coordinate(latitude = 33.309422, longitude = -119.7577028),
            northeast = Coordinate(latitude = 33.4151024, longitude = -119.6314974)
        )

        Assert.assertFalse(boundingBox.contains(polygon2))
    }

    @Test
    fun `Polygons app generated are inside bounding box 3`() {
        val polygon3 = BoundingBox(
            southwest = Coordinate(latitude = 33.163493, longitude = -119.636302),
            northeast = Coordinate(latitude = 33.335259, longitude = -119.360133)
        )

        Assert.assertFalse(boundingBox.contains(polygon3))
    }

    @Test
    fun `Polygons app generated are inside bounding box 4`() {
        val polygon4 = BoundingBox(
            southwest = Coordinate(latitude = 33.4083677, longitude = -119.1235599),
            northeast = Coordinate(latitude = 33.5402443, longitude = -118.9633999)
        )

        Assert.assertFalse(boundingBox.contains(polygon4))
    }

    @Test
    fun `Polygons app generated are inside bounding box 5`() {
        val polygon5 = BoundingBox(
            southwest = Coordinate(latitude = 32.75004, longitude = -118.678551),
            northeast = Coordinate(latitude = 33.087111, longitude = -118.288784)
        )

        Assert.assertFalse(boundingBox.contains(polygon5))
    }

    @Test
    fun `Polygons app generated are inside bounding box 6`() {
        val polygon6 = BoundingBox(
            southwest = Coordinate(latitude = 33.2480196, longitude = -118.6679611),
            northeast = Coordinate(latitude = 33.5323733, longitude = -118.2412195)
        )

        Assert.assertFalse(boundingBox.contains(polygon6))
    }

    @Test
    fun `Polygons app generated are inside bounding box 7`() {
        val polygon7 = BoundingBox(
            southwest = Coordinate(latitude = 28.1837783, longitude = -178.6015532),
            northeast = Coordinate(latitude = 28.6570819, longitude = -178.0560849)
        )

        Assert.assertTrue(boundingBox.contains(polygon7))
    }

    @Test
    fun `Polygons app generated are inside bounding box 8`() {
        val polygon8 = BoundingBox(
            southwest = Coordinate(latitude = 27.9911406, longitude = -177.6505562),
            northeast = Coordinate(latitude = 28.4801332, longitude = -177.0875122)
        )

        Assert.assertTrue(boundingBox.contains(polygon8))
    }

    @Test
    fun `Polygons app generated are inside bounding box 9`() {
        val polygon9 = BoundingBox(
            southwest = Coordinate(latitude = 27.5543172, longitude = -176.2166086),
            northeast = Coordinate(latitude = 28.1630636, longitude = -175.4983987)
        )

        Assert.assertTrue(boundingBox.contains(polygon9))
    }

    @Test
    fun `Polygons app generated are inside bounding box 10`() {
        val polygon10 = BoundingBox(
            southwest = Coordinate(latitude = 25.8411597, longitude = -174.2323557),
            northeast = Coordinate(latitude = 26.2722526, longitude = -173.7380378)
        )

        Assert.assertTrue(boundingBox.contains(polygon10))
    }

    @Test
    fun `Polygons app generated are inside bounding box 11`() {
        val polygon11 = BoundingBox(
            southwest = Coordinate(latitude = 25.555318, longitude = -171.967476),
            northeast = Coordinate(latitude = 25.9866618, longitude = -171.4949295)
        )

        Assert.assertTrue(boundingBox.contains(polygon11))
    }

    @Test
    fun `Polygons app generated are inside bounding box 12`() {
        val polygon12 = BoundingBox(
            southwest = Coordinate(latitude = 25.3053671, longitude = -170.8506754),
            northeast = Coordinate(latitude = 25.7076006, longitude = -170.4076102)
        )

        Assert.assertTrue(boundingBox.contains(polygon12))
    }
    @Test
    fun `Polygons app generated are inside bounding box 13`() {
        val polygon13 = BoundingBox(
            southwest = Coordinate(latitude = 16.5056303, longitude = -169.7705448),
            northeast = Coordinate(latitude = 16.9906002, longitude = -169.2522753)
        )

        Assert.assertFalse(boundingBox.contains(polygon13))
    }

    @Test
    fun `Polygons app generated are inside bounding box 14`() {
        val polygon14 = BoundingBox(
            southwest = Coordinate(latitude = 24.7968943, longitude = -168.2206809),
            northeast = Coordinate(latitude = 25.2009346, longitude = -167.778192)
        )

        Assert.assertFalse(boundingBox.contains(polygon14))
    }

    @Test
    fun `Polygons app generated are inside bounding box 15`() {
        val polygon15 = BoundingBox(
            southwest = Coordinate(latitude = 23.4254685, longitude = -166.5444718),
            northeast = Coordinate(latitude = 24.0769321, longitude = -165.83618)
        )

        Assert.assertFalse(boundingBox.contains(polygon15))
    }

    @Test
    fun `Polygons app generated are inside bounding box 16`() {
        val polygon16 = BoundingBox(
            southwest = Coordinate(latitude = 23.3734366, longitude = -164.9238978),
            northeast = Coordinate(latitude = 23.7797249, longitude = -164.4760626)
        )

        Assert.assertFalse(boundingBox.contains(polygon16))
    }

    @Test
    fun `Polygons app generated are inside bounding box 17`() {
        val polygon17 = BoundingBox(
            southwest = Coordinate(latitude = 22.8550496, longitude = -162.1456702),
            northeast = Coordinate(latitude = 23.2654049, longitude = -161.697168)
        )

        Assert.assertFalse(boundingBox.contains(polygon17))
    }

    @Test
    fun `Polygons app generated are inside bounding box 18`() {
        val polygon18 = BoundingBox(
            southwest = Coordinate(latitude = 21.4482644, longitude = -160.7601354),
            northeast = Coordinate(latitude = 22.4358477, longitude = -159.0764163)
        )

        Assert.assertFalse(boundingBox.contains(polygon18))
    }

    @Test
    fun `Polygons app generated are inside bounding box 19`() {
        val polygon19 = BoundingBox(
            southwest = Coordinate(latitude = 20.3000654, longitude = -158.4969099),
            northeast = Coordinate(latitude = 21.9142256, longitude = -155.7617864)
        )

        Assert.assertFalse(boundingBox.contains(polygon19))
    }

    @Test
    fun `Polygons app generated are inside bounding box 20`() {
        val polygon20 = BoundingBox(
            southwest = Coordinate(latitude = 18.7091718, longitude = -156.2746163),
            northeast = Coordinate(latitude = 20.4693439, longitude = -154.5942919)
        )

        Assert.assertFalse(boundingBox.contains(polygon20))
    }

    @Test
    fun `Polygons app generated are inside bounding box 21`() {
        val polygon21 = BoundingBox(
            southwest = Coordinate(latitude = 24.2520071, longitude = -125.0840939),
            northeast = Coordinate(latitude = 49.3844722, longitude = -66.8854156)
        )

        Assert.assertFalse(boundingBox.contains(polygon21))
    }

    @Test
    fun `Polygons app generated are inside bounding box 22`() {
        val polygon22 = BoundingBox(
            southwest = Coordinate(latitude = 24.4116731, longitude = -83.16255),
            northeast = Coordinate(latitude = 24.8516888, longitude = -82.5850092)
        )

        Assert.assertFalse(boundingBox.contains(polygon22))
    }

    @Test
    fun `Polygons app generated are inside bounding box 23`() {
        val polygon23 = BoundingBox(
            southwest = Coordinate(latitude = 18.1892548, longitude = -75.2384618),
            northeast = Coordinate(latitude = 18.6137166, longitude = -74.790449)
        )

        Assert.assertFalse(boundingBox.contains(polygon23))
    }

    @Test
    fun `Polygons app generated are inside bounding box 24`() {
        val polygon24 = BoundingBox(
            southwest = Coordinate(latitude = 17.8507179, longitude = -68.16127),
            northeast = Coordinate(latitude = 18.3629246, longitude = -67.6337618)
        )

        Assert.assertFalse(boundingBox.contains(polygon24))
    }

    @Test
    fun `Polygons app generated are inside bounding box 25`() {
        val polygon25 = BoundingBox(
            southwest = Coordinate(latitude = 17.6802716, longitude = -67.7012233),
            northeast = Coordinate(latitude = 18.7175521, longitude = -64.6369444)
        )
        Assert.assertFalse(boundingBox.contains(polygon25))
    }

    @Test
    fun `Polygons app generated are inside bounding box 26`() {
        val polygon26 = BoundingBox(
            southwest = Coordinate(latitude = 17.473097, longitude = -65.1138725),
            northeast = Coordinate(latitude = 17.99584, longitude = -64.35549)
        )

        Assert.assertFalse(boundingBox.contains(polygon26))
    }
}