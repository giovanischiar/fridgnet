package io.schiar.fridgnet.model.repository

import io.schiar.fridgnet.Log
import io.schiar.fridgnet.model.AdministrativeLevel
import io.schiar.fridgnet.model.AdministrativeLevel.CITY
import io.schiar.fridgnet.model.AdministrativeUnit
import io.schiar.fridgnet.model.datasource.AdministrativeUnitDataSource
import io.schiar.fridgnet.model.datasource.ImageDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject


class AdministrativeUnitsRepository @Inject constructor(
    private val administrativeUnitDataSource: AdministrativeUnitDataSource,
    private val imageDataSource: ImageDataSource
) {
    private var administrativeUnits: List<AdministrativeUnit> = emptyList()
    private val currentAdministrativeLevelStateFlow = MutableStateFlow(CITY)

    val administrativeLevelsFlow: Flow<List<AdministrativeLevel>> = MutableStateFlow(
        AdministrativeLevel.entries
    )
    val currentAdministrativeLevelFlow: Flow<AdministrativeLevel> = run {
        currentAdministrativeLevelStateFlow
    }
    @OptIn(ExperimentalCoroutinesApi::class)
    val administrativeUnitsFlow = currentAdministrativeLevelFlow
        .flatMapLatest { administrativeLevel ->
            administrativeUnitDataSource.retrieve(administrativeLevel)
        }
        .onEach { administrativeUnits -> this.administrativeUnits = administrativeUnits }

    fun selectAdministrativeUnitAt(index: Int) {
        val administrativeUnit = administrativeUnits[index]
        log(msg = "AdministrativeUnit at $index is $administrativeUnit")
        administrativeUnitDataSource.updateCurrentIndex(index)
    }

    fun changeCurrentAdministrativeLevel(index: Int) {
        currentAdministrativeLevelStateFlow.update { AdministrativeLevel.entries[index] }
    }

    suspend fun removeAllImages() { imageDataSource.delete() }

    private fun log(msg: String) {
        val methodName = Thread.currentThread().stackTrace[3].methodName
        Log.d(tag = "HomeRepository.$methodName", msg = msg)
    }
}