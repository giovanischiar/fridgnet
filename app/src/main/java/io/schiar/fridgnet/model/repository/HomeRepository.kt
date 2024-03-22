package io.schiar.fridgnet.model.repository

import io.schiar.fridgnet.Log
import io.schiar.fridgnet.library.util.IdentitySet
import io.schiar.fridgnet.model.AdministrativeLevel
import io.schiar.fridgnet.model.AdministrativeLevel.CITY
import io.schiar.fridgnet.model.AdministrativeLevel.COUNTRY
import io.schiar.fridgnet.model.AdministrativeLevel.COUNTY
import io.schiar.fridgnet.model.AdministrativeLevel.STATE
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
            createAdministrativeUnits(name = administrativeUnitNameRetrieved, image = image)
        }
    }

    private fun createAdministrativeUnits(name: AdministrativeUnitName, image: Image) {
        val country = createAdministrativeUnit(
            administrativeUnitName = name,
            administrativeLevel = COUNTRY,
            image = image
        )

        val state = createAdministrativeUnit(
            administrativeUnitName = name,
            administrativeLevel = STATE,
            image = image
        )

        val county = createAdministrativeUnit(
            administrativeUnitName = name,
            administrativeLevel = COUNTY,
            image = image
        )

        val city = createAdministrativeUnit(
            administrativeUnitName = name,
            administrativeLevel = CITY,
            image = image
        )

        if (county.subAdministrativeUnits.add(element = city)) {
            log(
                method = "onEachAdministrativeUnitNameAndImage",
                msg = "Adding the ${city.administrativeLevel} ${city.firstName} " +
                        "to the ${county.administrativeLevel} ${county.firstName}"
            )
        }

        if (state.subAdministrativeUnits.add(element = county)) {
            log(
                method = "onEachAdministrativeUnitNameAndImage",
                msg ="Adding the ${county.administrativeLevel} ${county.firstName} " +
                        "to the ${state.administrativeLevel} ${state.firstName}"
            )
        }

        if (country.subAdministrativeUnits.add(element = state)) {
            log(
                method = "onEachAdministrativeUnitNameAndImage",
                msg = "Adding the ${state.administrativeLevel} ${state.firstName} " +
                        "to the ${country.administrativeLevel} ${country.firstName}"
            )
        }
    }

    private fun createAdministrativeUnit(
        administrativeUnitName: AdministrativeUnitName,
        administrativeLevel: AdministrativeLevel,
        image: Image
    ): AdministrativeUnit {
        val administrativeLevelWithName = administrativeLevel.with(
            administrativeUnitName = administrativeUnitName
        )
        val administrativeUnit = administrativeUnitByName[administrativeLevelWithName]
        return if (administrativeUnit == null) {
            val newAdministrativeUnit = AdministrativeUnit(
                name = administrativeUnitName.toString(administrativeLevel = administrativeLevel),
                administrativeLevel = administrativeLevel,
                subAdministrativeUnits = IdentitySet(),
                images = mutableSetOf(image)
            )
            administrativeUnitByName[administrativeLevelWithName] = newAdministrativeUnit
            administrativeUnitNamesByAdministrativeLevel[administrativeLevel]?.add(
                administrativeLevelWithName
            )
            newAdministrativeUnit
        } else {
            administrativeUnit.images.add(image)
            administrativeUnit
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
        val administrativeLevel = cartographicBoundary.administrativeLevel
        val administrationLevelWithName = cartographicBoundary.administrationLevelWithName
        val administrativeUnit = administrativeUnitByName[administrationLevelWithName]
        if (administrativeUnit == null) {
            administrativeUnitByName[administrationLevelWithName] = AdministrativeUnit(
                name = cartographicBoundary.administrativeUnitNameString,
                administrativeLevel = cartographicBoundary.administrativeLevel,
                cartographicBoundary = cartographicBoundary
            )
            administrativeUnitNamesByAdministrativeLevel[administrativeLevel]?.add(
                administrationLevelWithName
            )
        } else {
            administrativeUnit.cartographicBoundary = cartographicBoundary
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
                it.administrationLevelWithName
            }
        )

        val administrativeLevelsWithMissingCartographicBoundary = _administrativeLevels.filter {
            administrativeLevel -> run {
                val administrativeLevelWithName = administrativeLevel.with(
                    administrativeUnitName = administrativeUnitName
                )
                !administrativeUnitNameRetrievingCartographicBoundarySet.contains(
                    administrativeLevelWithName
                )
            }
        }

        for (administrativeLevel in administrativeLevelsWithMissingCartographicBoundary) {
            val administrativeLevelWithName = administrativeLevel.with(
                administrativeUnitName = administrativeUnitName
            )
            administrativeUnitNameRetrievingCartographicBoundarySet.add(
                administrativeLevelWithName
            )
            externalScope.launch {
                log(
                    method = "retrieveCartographicBoundariesForAdministrativeUnitName",
                    msg = "Retrieve Cartographic Boundary for the $administrativeLevel ${
                        administrativeUnitName.toString(administrativeLevel = administrativeLevel)
                    }"
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