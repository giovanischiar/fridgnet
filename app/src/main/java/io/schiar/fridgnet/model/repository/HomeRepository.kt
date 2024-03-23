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
import io.schiar.fridgnet.model.datasource.retriever.AdministrativeUnitNameRetriever
import io.schiar.fridgnet.model.datasource.retriever.CartographicBoundaryRetriever
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Collections.synchronizedList as syncListOf
import java.util.Collections.synchronizedMap as syncMapOf
import java.util.Collections.synchronizedSet as syncSetOf

class HomeRepository(
    private val administrativeUnitNameRetriever: AdministrativeUnitNameRetriever,
    private val administrativeUnitNameDataSource: AdministrativeUnitNameDataSource,
    private val cartographicBoundaryRetriever: CartographicBoundaryRetriever,
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
            .onEach {
                val (image, administrativeUnitName) = it
                if (administrativeUnitName != null) {
                    createAdministrativeUnits(administrativeUnitName, image)
                    return@onEach
                }
                externalScope.launch {
                    retrieveAdministrativeUnitNameForGeoLocation(it)
                        .onEach(::createAdministrativeUnitName)
                        .collect()
                }
            },
        administrativeUnitNameDataSource.retrieve()
            .onEach {
                externalScope.launch {
                    retrieveCartographicBoundariesForAdministrativeUnitName(it)
                        .onEach(::onEachCartographicBoundary)
                        .onEach(cartographicBoundaryDataSource::create)
                        .collect()
                }
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

    private fun onEachImageAndAdministrativeUnitName(
        imageAndAdministrativeUnitName: Pair<Image, AdministrativeUnitName?>
    ) {
        val (image, administrativeUnitNameRetrieved) = imageAndAdministrativeUnitName
        externalScope.launch {
            retrieveAdministrativeUnitNameForGeoLocation(imageAndAdministrativeUnitName)
                .onEach(::createAdministrativeUnitName)
                .collect()
        }
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
                msg = "Adding the ${city.administrativeLevel} ${city.firstName} " +
                        "to the ${county.administrativeLevel} ${county.firstName}"
            )
        }

        if (state.subAdministrativeUnits.add(element = county)) {
            log(
                msg ="Adding the ${county.administrativeLevel} ${county.firstName} " +
                        "to the ${state.administrativeLevel} ${state.firstName}"
            )
        }

        if (country.subAdministrativeUnits.add(element = state)) {
            log(
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
        log(msg = "AdministrativeUnit at $index is $administrativeUnit")
        currentAdministrativeUnitDataSource.update(administrativeUnit = administrativeUnit)
    }

    fun changeCurrentAdministrativeLevel(index: Int) {
        _currentAdministrativeLevelStateFlow.update { _administrativeLevels[index] }
    }

    suspend fun removeAllImages() { imageDataSource.delete() }

    private fun retrieveAdministrativeUnitNameForGeoLocation(
        imageAndAdministrativeUnitName: Pair<Image, AdministrativeUnitName?>
    ): Flow<Pair<GeoLocation, AdministrativeUnitName>> {
        val (image, administrationUnitName) = imageAndAdministrativeUnitName
        val geoLocation = image.geoLocation
        if (administrationUnitName != null) {
            geoLocationRetrievingAdministrativeUnitNameSet.add(element = geoLocation)
        }
        return if (!geoLocationRetrievingAdministrativeUnitNameSet.contains(element = geoLocation)) {
            administrativeUnitNameRetriever.retrieve(geoLocation = geoLocation)
        } else {
            flowOf(value = null)
        }.mapNotNull { it }
    }

    private fun retrieveCartographicBoundariesForAdministrativeUnitName(
        administrativeUnitNameAndCartographicBoundaries: Pair<
            AdministrativeUnitName, List<CartographicBoundary>
        >
    ): Flow<CartographicBoundary> {
        val (
            administrativeUnitName, cartographicBoundariesFromAdministrativeUnitName
        ) = administrativeUnitNameAndCartographicBoundaries
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

        val administrativeUnitLevelAndAdministrativeUnitNameList
            = administrativeLevelsWithMissingCartographicBoundary
                .map { administrativeLevel ->
                    val administrativeLevelWithName = administrativeLevel.with(
                        administrativeUnitName = administrativeUnitName
                    )
                    administrativeUnitNameRetrievingCartographicBoundarySet.add(
                        administrativeLevelWithName
                    )
                    Pair(administrativeLevel, administrativeUnitName)
                }

        return cartographicBoundaryRetriever.retrieve(
            administrativeUnitLevelAndAdministrativeUnitNameList
        )
    }

    private suspend fun createAdministrativeUnitName(
        geoLocationAndAdministrativeUnitName: Pair<GeoLocation, AdministrativeUnitName>
    ) {
        val (geoLocation, administrativeUnitName) = geoLocationAndAdministrativeUnitName
        administrativeUnitNameDataSource.create(
            geoLocation = geoLocation,
            administrativeUnitName = administrativeUnitName
        )
    }

    private fun log(msg: String) {
        val methodName = Thread.currentThread().stackTrace[3].methodName
        Log.d(tag = "HomeRepository.$methodName", msg = msg)
    }
}