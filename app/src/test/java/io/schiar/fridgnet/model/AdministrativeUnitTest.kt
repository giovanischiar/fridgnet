package io.schiar.fridgnet.model

import org.junit.Assert
import org.junit.Test

class AdministrativeUnitTest {
    @Test
    fun `AdministrativeUnit with everything null`() {
        val administrativeUnit = AdministrativeUnit(
            locality = null,
            subAdminArea = null,
            adminArea = null,
            countryName = null
        )

        val expected = ""
        Assert.assertEquals(expected, administrativeUnit.name())
    }

    @Test
    fun `AdministrativeUnit with subAdminArea, adminArea and countryName null`() {
        val administrativeUnit = AdministrativeUnit(
            locality = "Sunnyvale",
            subAdminArea = null,
            adminArea = null,
            countryName = null
        )

        val expected = "Sunnyvale"
        Assert.assertEquals(expected, administrativeUnit.name())
    }

    @Test
    fun `AdministrativeUnit with locality, adminArea and countryName null`() {
        val administrativeUnit = AdministrativeUnit(
            locality = null,
            subAdminArea = "Santa Clara County",
            adminArea = null,
            countryName = null
        )

        val expected = "Santa Clara County"
        Assert.assertEquals(expected, administrativeUnit.name())
    }

    @Test
    fun `AdministrativeUnit with AdminArea and countryName null`() {
        val administrativeUnit = AdministrativeUnit(
            locality = "Sunnyvale",
            subAdminArea = "Santa Clara County",
            adminArea = null,
            countryName = null
        )

        val expected = "Sunnyvale, Santa Clara County"
        Assert.assertEquals(expected, administrativeUnit.name())
    }

    @Test
    fun `AdministrativeUnit with Locality, subAdminArea and countryName null`() {
        val administrativeUnit = AdministrativeUnit(
            locality = null,
            subAdminArea = null,
            adminArea = "California",
            countryName = null
        )

        val expected = "California"
        Assert.assertEquals(expected, administrativeUnit.name())
    }

    @Test
    fun `AdministrativeUnit with subAdminArea and countryName null`() {
        val administrativeUnit = AdministrativeUnit(
            locality = "Sunnyvale",
            subAdminArea = null,
            adminArea = "California",
            countryName = null
        )

        val expected = "Sunnyvale, California"
        Assert.assertEquals(expected, administrativeUnit.name())
    }

    @Test
    fun `AdministrativeUnit with countryName null`() {
        val administrativeUnit = AdministrativeUnit(
            locality = "Sunnyvale",
            subAdminArea = "Santa Clara County",
            adminArea = "California",
            countryName = null
        )

        val expected = "Sunnyvale, Santa Clara County, California"
        Assert.assertEquals(expected, administrativeUnit.name())
    }

    @Test
    fun `AdministrativeUnit with locality, subAdminArea and adminArea null`() {
        val administrativeUnit = AdministrativeUnit(
            locality = null,
            subAdminArea = null,
            adminArea = null,
            countryName = "United States"
        )

        val expected = "United States"
        Assert.assertEquals(expected, administrativeUnit.name())
    }

    @Test
    fun `AdministrativeUnit with locality and country null`() {
        val administrativeUnit = AdministrativeUnit(
            locality = "Sunnyvale",
            subAdminArea = null,
            adminArea = null,
            countryName = "United States"
        )

        val expected = "Sunnyvale, United States"
        Assert.assertEquals(expected, administrativeUnit.name())
    }

    @Test
    fun `AdministrativeUnit with locality and adminArea null`() {
        val administrativeUnit = AdministrativeUnit(
            locality = null,
            subAdminArea = "Santa Clara County",
            adminArea = null,
            countryName = "United States"
        )

        val expected = "Santa Clara County, United States"
        Assert.assertEquals(expected, administrativeUnit.name())
    }

    @Test
    fun `AdministrativeUnit with adminArea null`() {
        val administrativeUnit = AdministrativeUnit(
            locality = "Sunnyvale",
            subAdminArea = "Santa Clara County",
            adminArea = null,
            countryName = "United States"
        )

        val expected = "Sunnyvale, Santa Clara County, United States"
        Assert.assertEquals(expected, administrativeUnit.name())
    }

    @Test
    fun `AdministrativeUnit with locality and subAdminArea null`() {
        val administrativeUnit = AdministrativeUnit(
            locality = null,
            subAdminArea = null,
            adminArea = "California",
            countryName = "United States"
        )

        val expected = "California, United States"
        Assert.assertEquals(expected, administrativeUnit.name())
    }

    @Test
    fun `AdministrativeUnit with subAdminArea null`() {
        val administrativeUnit = AdministrativeUnit(
            locality = "Sunnyvale",
            subAdminArea = null,
            adminArea = "California",
            countryName = "United States"
        )

        val expected = "Sunnyvale, California, United States"
        Assert.assertEquals(expected, administrativeUnit.name())
    }

    @Test
    fun `AdministrativeUnit with locality null`() {
        val administrativeUnit = AdministrativeUnit(
            locality = null,
            subAdminArea = "Santa Clara County",
            adminArea = "California",
            countryName = "United States"
        )

        val expected = "Santa Clara County, California, United States"
        Assert.assertEquals(expected, administrativeUnit.name())
    }

    @Test
    fun `AdministrativeUnit with nothing null`() {
        val administrativeUnit = AdministrativeUnit(
            locality = "Sunnyvale",
            subAdminArea = "Santa Clara County",
            adminArea = "California",
            countryName = "United States"
        )

        val expected = "Sunnyvale, Santa Clara County, California, United States"
        Assert.assertEquals(expected, administrativeUnit.name())
    }
}