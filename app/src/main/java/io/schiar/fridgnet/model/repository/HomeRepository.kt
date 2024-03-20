package io.schiar.fridgnet.model.repository

import io.schiar.fridgnet.Log
import io.schiar.fridgnet.model.AdminUnit
import io.schiar.fridgnet.model.AdministrativeLevel
import io.schiar.fridgnet.model.AdministrativeLevel.CITY
import io.schiar.fridgnet.model.AdministrativeUnit
import io.schiar.fridgnet.model.CartographicBoundary
import io.schiar.fridgnet.model.GeoLocation
import io.schiar.fridgnet.model.Image
import io.schiar.fridgnet.model.datasource.AdministrativeUnitDataSource
import io.schiar.fridgnet.model.datasource.CartographicBoundaryDataSource
import io.schiar.fridgnet.model.datasource.CurrentAdminUnitDataSource
import io.schiar.fridgnet.model.datasource.ImageDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Collections.synchronizedList as syncListOf
import java.util.Collections.synchronizedMap as syncMapOf
import java.util.Collections.synchronizedSet as syncSetOf

class HomeRepository(
    private val administrativeUnitDataSource: AdministrativeUnitDataSource,
    private val cartographicBoundaryDataSource: CartographicBoundaryDataSource,
    private val imageDataSource: ImageDataSource,
    private val currentAdminUnitDataSource : CurrentAdminUnitDataSource,
    private val externalScope: CoroutineScope
) {
    private var _currentAdminUnits = emptyList<AdminUnit>()
    private val _administrativeLevels = AdministrativeLevel.entries
    private val adminUnitByName = syncMapOf(mutableMapOf<String, AdminUnit>())
    private val adminUnitNamesByAdministrativeLevel = run {
        syncMapOf(_administrativeLevels.associateWith { syncListOf(mutableListOf<String>()) })
    }

    private var _currentAdministrativeLevel = CITY

    private val _currentAdministrativeLevelStateFlow = MutableStateFlow(_currentAdministrativeLevel)
    val administrativeLevels: Flow<List<AdministrativeLevel>>
        = MutableStateFlow(_administrativeLevels)
    val currentAdministrativeLevel: Flow<AdministrativeLevel>
        = _currentAdministrativeLevelStateFlow

    private val geoLocationRetrievingAdministrativeUnitSet = syncSetOf(mutableSetOf<GeoLocation>())
    private val administrativeUnitRetrievingCartographicBoundarySet
        = syncSetOf(mutableSetOf<String>())

    val adminUnits = merge(
        imageDataSource.retrieveWithAdministrativeUnit().onEach { imageAdministrativeUnits ->
            imageAdministrativeUnits.forEach(::onEachAdministrativeUnitAndImage)
        },
        administrativeUnitDataSource.retrieve()
            .onEach { administrativeUnitAndCartographicBoundariesList ->
                administrativeUnitAndCartographicBoundariesList.forEach(
                    ::onEachAdministrativeUnitAndCartographicBoundaries
                )
        },
        cartographicBoundaryDataSource.retrieve().onEach { cartographicBoundaries ->
            cartographicBoundaries.forEach(::onEachCartographicBoundary)
        },
        _currentAdministrativeLevelStateFlow.onEach { _currentAdministrativeLevel = it }
    ).map { getAdminUnits() }.onEach { _currentAdminUnits = it }

    private fun getAdminUnits(): List<AdminUnit> {
        val adminUnitNames
            = adminUnitNamesByAdministrativeLevel[_currentAdministrativeLevel] ?: return emptyList()
        return adminUnitNames.mapNotNull { adminUnitName -> adminUnitByName[adminUnitName] }
    }

    private fun onEachAdministrativeUnitAndImage(
        administrativeUnitAndImage: Pair<AdministrativeUnit?, Image>
    ) {
        val (administrativeUnitRetrieved, image) = administrativeUnitAndImage
        retrieveAdministrativeUnitForGeoLocation(
            geoLocation = image.geoLocation,
            administrativeUnitFromGeoLocation = administrativeUnitRetrieved
        )

        if (administrativeUnitRetrieved != null) {
            val administrativeUnitName = administrativeUnitRetrieved.name()
            if (!adminUnitByName.containsKey(administrativeUnitName)) {
                adminUnitByName[administrativeUnitRetrieved.name()] = AdminUnit(
                    name = administrativeUnitName,
                    administrativeLevel = CITY,
                    subAdministrativeUnits = emptyList(),
                    images = mutableListOf(image)
                )
                adminUnitNamesByAdministrativeLevel[CITY]?.add(administrativeUnitName)
            } else {
                adminUnitByName[administrativeUnitRetrieved.name()]?.images?.add(image)
            }
            return
        }
    }

    private fun onEachAdministrativeUnitAndCartographicBoundaries(
        administrativeUnitAndCartographicCoordinates
            : Pair<AdministrativeUnit, List<CartographicBoundary>>
    ) {
        val (
            administrativeUnit, cartographicBoundariesRetrieved
        ) = administrativeUnitAndCartographicCoordinates
        retrieveCartographicBoundariesForAdministrativeUnit(
            administrativeUnit = administrativeUnit,
            cartographicBoundariesFromAdministrativeUnit = cartographicBoundariesRetrieved
        )
    }

    private fun onEachCartographicBoundary(cartographicBoundary: CartographicBoundary) {
        val administrativeUnitName = cartographicBoundary.administrativeUnitName()
        val administrativeLevel = cartographicBoundary.administrativeLevel
        if (!adminUnitByName.containsKey(administrativeUnitName)) {
            adminUnitByName[administrativeUnitName] = AdminUnit(
                name = administrativeUnitName,
                administrativeLevel = cartographicBoundary.administrativeLevel,
                cartographicBoundary = cartographicBoundary
            )
            adminUnitNamesByAdministrativeLevel[administrativeLevel]?.add(administrativeUnitName)
        } else {
            adminUnitByName[administrativeUnitName]?.cartographicBoundary = cartographicBoundary
        }
    }

    fun selectAdminUnitAt(index: Int) {
        val adminUnit = _currentAdminUnits[index]
        log(method = "selectAdminUnitAt", msg = "AdminUnit at $index is $adminUnit")
        currentAdminUnitDataSource.update(adminUnit = adminUnit)
    }

    fun changeCurrentAdministrativeLevel(index: Int) {
        _currentAdministrativeLevelStateFlow.update { _administrativeLevels[index] }
    }

    suspend fun removeAllImages() { imageDataSource.delete() }

    private fun retrieveAdministrativeUnitForGeoLocation(
        geoLocation: GeoLocation, administrativeUnitFromGeoLocation: AdministrativeUnit?
    ) {
        if (administrativeUnitFromGeoLocation != null) {
            geoLocationRetrievingAdministrativeUnitSet.add(geoLocation)
        }
        val administrativeUnitNotBeingRetrieved =
            !geoLocationRetrievingAdministrativeUnitSet.contains(geoLocation)
        if (administrativeUnitNotBeingRetrieved) {
            geoLocationRetrievingAdministrativeUnitSet.add(element = geoLocation)
            log(
                method = "retrieveAdministrativeUnitForGeoLocation",
                msg = "Retrieve AdministrativeUnit for $geoLocation"
            )
            externalScope.launch {
                administrativeUnitDataSource.retrieveAdministrativeUnitFor(
                    geoLocation = geoLocation
                )
            }
        }
    }

    private fun retrieveCartographicBoundariesForAdministrativeUnit(
        administrativeUnit: AdministrativeUnit,
        cartographicBoundariesFromAdministrativeUnit: Collection<CartographicBoundary>
    ) {
        administrativeUnitRetrievingCartographicBoundarySet.addAll(
            cartographicBoundariesFromAdministrativeUnit.map { it.administrativeUnitName() }
        )

        val administrativeLevelsWithNonRetrievedLocation = _administrativeLevels.filter {
                administrativeLevel -> run {
                val administrativeUnitName = administrativeUnit.name(
                    administrativeLevel = administrativeLevel
                )
                !administrativeUnitRetrievingCartographicBoundarySet.contains(
                    administrativeUnitName
                )
            }
        }

        for (administrativeLevel in administrativeLevelsWithNonRetrievedLocation) {
            val administrativeUnitName = administrativeUnit.name(
                administrativeLevel = administrativeLevel
            )
            administrativeUnitRetrievingCartographicBoundarySet.add(administrativeUnitName)
            externalScope.launch {
                log(
                    method = "retrieveCartographicBoundariesForAdministrativeUnit",
                    msg = "Retrieve Cartographic Boundary for $administrativeUnitName"
                )
                cartographicBoundaryDataSource.retrieveLocationFor(
                    administrativeUnit = administrativeUnit,
                    administrativeLevel = administrativeLevel
                )
            }
        }
    }

    private fun log(method: String, msg: String) {
        Log.d(tag = "HomeRepository.$method", msg = msg)
    }
}