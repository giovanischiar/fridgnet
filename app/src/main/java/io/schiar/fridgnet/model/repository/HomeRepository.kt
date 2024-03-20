package io.schiar.fridgnet.model.repository

import io.schiar.fridgnet.Log
import io.schiar.fridgnet.model.AdministrativeLevel
import io.schiar.fridgnet.model.AdministrativeLevel.CITY
import io.schiar.fridgnet.model.AdministrativeUnit
import io.schiar.fridgnet.model.AdministrativeUnitName
import io.schiar.fridgnet.model.CartographicBoundary
import io.schiar.fridgnet.model.GeoLocation
import io.schiar.fridgnet.model.Image
import io.schiar.fridgnet.model.datasource.AdministrativeUnitNameDataSource
import io.schiar.fridgnet.model.datasource.CartographicBoundaryDataSource
import io.schiar.fridgnet.model.datasource.CurrentAdministrativeUnitDataSource
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
    private val currentAdministrativeUnitDataSource : CurrentAdministrativeUnitDataSource,
    private val externalScope: CoroutineScope
) {
    private var _currentAdministrativeUnits = emptyList<AdministrativeUnit>()
    private val _administrativeLevels = AdministrativeLevel.entries
    private val administrativeUnitByName = syncMapOf(mutableMapOf<String, AdministrativeUnit>())
    private val administrativeUnitNamesByAdministrativeLevel = run {
        syncMapOf(_administrativeLevels.associateWith { syncListOf(mutableListOf<String>()) })
    }

    private var _currentAdministrativeLevel = CITY

    private val _currentAdministrativeLevelStateFlow = MutableStateFlow(_currentAdministrativeLevel)
    val administrativeLevels: Flow<List<AdministrativeLevel>>
        = MutableStateFlow(_administrativeLevels)
    val currentAdministrativeLevel: Flow<AdministrativeLevel> = _currentAdministrativeLevelStateFlow

    private val geoLocationRetrievingAdministrativeUnitNameSet = syncSetOf(
        mutableSetOf<GeoLocation>()
    )
    private val administrativeUnitNameRetrievingCartographicBoundarySet = syncSetOf(
        mutableSetOf<String>()
    )

    val administrativeUnits = merge(
        imageDataSource.retrieveWithAdministrativeUnitName()
            .onEach { imageAndAdministrativeUnitNameList ->
                imageAndAdministrativeUnitNameList.forEach(::onEachAdministrativeUnitNameAndImage)
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
    ).map { getAdministrativeUnits() }.onEach { _currentAdministrativeUnits = it }

    private fun getAdministrativeUnits(): List<AdministrativeUnit> {
        val administrativeUnitNames = administrativeUnitNamesByAdministrativeLevel[
            _currentAdministrativeLevel
        ] ?: return emptyList()
        return administrativeUnitNames.mapNotNull {
            administrativeUnitName -> administrativeUnitByName[administrativeUnitName]
        }
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
            val administrativeUnitNameString = "$administrativeUnitNameRetrieved"
            if (!administrativeUnitByName.containsKey(administrativeUnitNameString)) {
                administrativeUnitByName[administrativeUnitNameString] = AdministrativeUnit(
                    name = administrativeUnitNameString,
                    administrativeLevel = CITY,
                    subAdministrativeUnitNames = emptyList(),
                    images = mutableListOf(image)
                )
                administrativeUnitNamesByAdministrativeLevel[CITY]?.add(
                    administrativeUnitNameString
                )
            } else {
                administrativeUnitByName[administrativeUnitNameString]?.images?.add(image)
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
        val administrativeUnitNameString = cartographicBoundary.administrativeUnitNameString()
        val administrativeLevel = cartographicBoundary.administrativeLevel
        if (!administrativeUnitByName.containsKey(administrativeUnitNameString)) {
            administrativeUnitByName[administrativeUnitNameString] = AdministrativeUnit(
                name = administrativeUnitNameString,
                administrativeLevel = cartographicBoundary.administrativeLevel,
                cartographicBoundary = cartographicBoundary
            )
            administrativeUnitNamesByAdministrativeLevel[administrativeLevel]?.add(
                administrativeUnitNameString
            )
        } else {
            administrativeUnitByName[
                administrativeUnitNameString
            ]?.cartographicBoundary = cartographicBoundary
        }
    }

    fun selectAdministrativeUnitAt(index: Int) {
        val administrativeUnit = _currentAdministrativeUnits[index]
        log(
            method = "selectAdministrativeUnitAt",
            msg = "AdministrativeUnit at $index is $administrativeUnit"
        )
        currentAdministrativeUnitDataSource.update(administrativeUnit = administrativeUnit)
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
            cartographicBoundariesFromAdministrativeUnitName.map {
                it.administrativeUnitNameString()
            }
        )

        val administrativeLevelsWithNonRetrievedLocation = _administrativeLevels.filter {
                administrativeLevel -> run {
                val administrativeUnitNameString = administrativeUnitName.toString(
                    administrativeLevel = administrativeLevel
                )
                !administrativeUnitNameRetrievingCartographicBoundarySet.contains(
                    administrativeUnitNameString
                )
            }
        }

        for (administrativeLevel in administrativeLevelsWithNonRetrievedLocation) {
            val administrativeUnitNameString = administrativeUnitName.toString(
                administrativeLevel = administrativeLevel
            )
            administrativeUnitNameRetrievingCartographicBoundarySet.add(
                administrativeUnitNameString
            )
            externalScope.launch {
                log(
                    method = "retrieveCartographicBoundariesForAdministrativeUnitName",
                    msg = "Retrieve Cartographic Boundary for $administrativeUnitNameString"
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