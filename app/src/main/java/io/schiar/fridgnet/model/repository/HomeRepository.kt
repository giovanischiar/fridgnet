package io.schiar.fridgnet.model.repository

import io.schiar.fridgnet.Log
import io.schiar.fridgnet.model.AdminUnit
import io.schiar.fridgnet.model.AdministrativeLevel
import io.schiar.fridgnet.model.AdministrativeLevel.CITY
import io.schiar.fridgnet.model.AdministrativeUnitName
import io.schiar.fridgnet.model.CartographicBoundary
import io.schiar.fridgnet.model.GeoLocation
import io.schiar.fridgnet.model.Image
import io.schiar.fridgnet.model.datasource.AdministrativeUnitNameDataSource
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
    private val administrativeUnitNameDataSource: AdministrativeUnitNameDataSource,
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

    private val geoLocationRetrievingAdministrativeUnitNameSet = syncSetOf(mutableSetOf<GeoLocation>())
    private val administrativeUnitNameRetrievingCartographicBoundarySet
        = syncSetOf(mutableSetOf<String>())

    val adminUnits = merge(
        imageDataSource.retrieveWithAdministrativeUnitName().onEach { imageAdministrativeUnitNames ->
            imageAdministrativeUnitNames.forEach(::onEachAdministrativeUnitNameAndImage)
        },
        administrativeUnitNameDataSource.retrieve()
            .onEach { administrativeUnitNameAndCartographicBoundariesList ->
                administrativeUnitNameAndCartographicBoundariesList.forEach(
                    ::onEachAdministrativeUnitNameAndCartographicBoundaries
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

    private fun onEachAdministrativeUnitNameAndImage(
        administrativeUnitNameAndImage: Pair<AdministrativeUnitName?, Image>
    ) {
        val (administrativeUnitNameRetrieved, image) = administrativeUnitNameAndImage
        retrieveAdministrativeUnitNameForGeoLocation(
            geoLocation = image.geoLocation,
            administrativeUnitNameFromGeoLocation = administrativeUnitNameRetrieved
        )

        if (administrativeUnitNameRetrieved != null) {
            val administrativeUnitNameName = administrativeUnitNameRetrieved.name()
            if (!adminUnitByName.containsKey(administrativeUnitNameName)) {
                adminUnitByName[administrativeUnitNameRetrieved.name()] = AdminUnit(
                    name = administrativeUnitNameName,
                    administrativeLevel = CITY,
                    subAdministrativeUnitNames = emptyList(),
                    images = mutableListOf(image)
                )
                adminUnitNamesByAdministrativeLevel[CITY]?.add(administrativeUnitNameName)
            } else {
                adminUnitByName[administrativeUnitNameRetrieved.name()]?.images?.add(image)
            }
            return
        }
    }

    private fun onEachAdministrativeUnitNameAndCartographicBoundaries(
        administrativeUnitNameAndCartographicCoordinates
            : Pair<AdministrativeUnitName, List<CartographicBoundary>>
    ) {
        val (
            administrativeUnitName, cartographicBoundariesRetrieved
        ) = administrativeUnitNameAndCartographicCoordinates
        retrieveCartographicBoundariesForAdministrativeUnitName(
            administrativeUnitName = administrativeUnitName,
            cartographicBoundariesFromAdministrativeUnitName = cartographicBoundariesRetrieved
        )
    }

    private fun onEachCartographicBoundary(cartographicBoundary: CartographicBoundary) {
        val administrativeUnitNameName = cartographicBoundary.administrativeUnitNameName()
        val administrativeLevel = cartographicBoundary.administrativeLevel
        if (!adminUnitByName.containsKey(administrativeUnitNameName)) {
            adminUnitByName[administrativeUnitNameName] = AdminUnit(
                name = administrativeUnitNameName,
                administrativeLevel = cartographicBoundary.administrativeLevel,
                cartographicBoundary = cartographicBoundary
            )
            adminUnitNamesByAdministrativeLevel[administrativeLevel]?.add(administrativeUnitNameName)
        } else {
            adminUnitByName[administrativeUnitNameName]?.cartographicBoundary = cartographicBoundary
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

    private fun retrieveAdministrativeUnitNameForGeoLocation(
        geoLocation: GeoLocation, administrativeUnitNameFromGeoLocation: AdministrativeUnitName?
    ) {
        if (administrativeUnitNameFromGeoLocation != null) {
            geoLocationRetrievingAdministrativeUnitNameSet.add(geoLocation)
        }
        val administrativeUnitNameNotBeingRetrieved =
            !geoLocationRetrievingAdministrativeUnitNameSet.contains(geoLocation)
        if (administrativeUnitNameNotBeingRetrieved) {
            geoLocationRetrievingAdministrativeUnitNameSet.add(element = geoLocation)
            log(
                method = "retrieveAdministrativeUnitNameForGeoLocation",
                msg = "Retrieve AdministrativeUnitName for $geoLocation"
            )
            externalScope.launch {
                administrativeUnitNameDataSource.retrieveAdministrativeUnitNameFor(
                    geoLocation = geoLocation
                )
            }
        }
    }

    private fun retrieveCartographicBoundariesForAdministrativeUnitName(
        administrativeUnitName: AdministrativeUnitName,
        cartographicBoundariesFromAdministrativeUnitName: Collection<CartographicBoundary>
    ) {
        administrativeUnitNameRetrievingCartographicBoundarySet.addAll(
            cartographicBoundariesFromAdministrativeUnitName.map { it.administrativeUnitNameName() }
        )

        val administrativeLevelsWithNonRetrievedLocation = _administrativeLevels.filter {
                administrativeLevel -> run {
                val administrativeUnitNameName = administrativeUnitName.name(
                    administrativeLevel = administrativeLevel
                )
                !administrativeUnitNameRetrievingCartographicBoundarySet.contains(
                    administrativeUnitNameName
                )
            }
        }

        for (administrativeLevel in administrativeLevelsWithNonRetrievedLocation) {
            val administrativeUnitNameName = administrativeUnitName.name(
                administrativeLevel = administrativeLevel
            )
            administrativeUnitNameRetrievingCartographicBoundarySet.add(administrativeUnitNameName)
            externalScope.launch {
                log(
                    method = "retrieveCartographicBoundariesForAdministrativeUnitName",
                    msg = "Retrieve Cartographic Boundary for $administrativeUnitNameName"
                )
                cartographicBoundaryDataSource.retrieveLocationFor(
                    administrativeUnitName = administrativeUnitName,
                    administrativeLevel = administrativeLevel
                )
            }
        }
    }

    private fun log(method: String, msg: String) {
        Log.d(tag = "HomeRepository.$method", msg = msg)
    }
}