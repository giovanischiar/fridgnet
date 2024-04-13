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

/**
 * The repository that exposes and manipulates the administrative units' screen flows. This
 * repository manages the data flow related to administrative units displayed on the screen and
 * provides methods for manipulating and accessing this data.
 */
class AdministrativeUnitsRepository @Inject constructor(
    private val administrativeUnitDataSource: AdministrativeUnitDataSource,
    private val imageDataSource: ImageDataSource
) {
    private var administrativeUnits: List<AdministrativeUnit> = emptyList()
    private val currentAdministrativeLevelStateFlow = MutableStateFlow(CITY)

    /**
     * The list of all possible administrative levels used in the dropdown in the administrative
     * unit screen
     */
    val administrativeLevelsFlow: Flow<List<AdministrativeLevel>> = MutableStateFlow(
        AdministrativeLevel.entries
    )
    /**
     * The current administrative level flow will update when the use choose a different level on
     * the dropdown in the administrative unit screen
     */
    val currentAdministrativeLevelFlow: Flow<AdministrativeLevel> = run {
        currentAdministrativeLevelStateFlow
    }

    /**
     * A flow representing the list of administrative units. It will be updated with the most recent
     * administrative units and will resend if the user changes the current administrative unit.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    val administrativeUnitsFlow = currentAdministrativeLevelFlow
        .flatMapLatest { administrativeLevel ->
            administrativeUnitDataSource.retrieve(administrativeLevel)
        }
        .onEach { administrativeUnits -> this.administrativeUnits = administrativeUnits }

    /**
     * When the user select an administrative unit in the grid this method will update the data
     * source with the index of the administrative unit selected.
     *
     * @param index the index of the administrative unit selected in the screen
     */
    fun selectAdministrativeUnitAt(index: Int) {
        val administrativeUnit = administrativeUnits[index]
        log(msg = "AdministrativeUnit at $index is $administrativeUnit")
        administrativeUnitDataSource.updateCurrentIndex(index)
    }

    /**
     * When the user select a different administrative level using the dropdown this method search
     * using the index and update the current administrative level. This triggers again the list
     * of administrative units flow updating the view.
     *
     * @param index the index of the administrative level selected in the screen
     */
    fun changeCurrentAdministrativeLevel(index: Int) {
        currentAdministrativeLevelStateFlow.update { AdministrativeLevel.entries[index] }
    }

    /**
     * Remove all images from data source.
     */
    suspend fun removeAllImages() { imageDataSource.delete() }

    private fun log(msg: String) {
        val methodName = Thread.currentThread().stackTrace[3].methodName
        Log.d(tag = "HomeRepository.$methodName", msg = msg)
    }
}