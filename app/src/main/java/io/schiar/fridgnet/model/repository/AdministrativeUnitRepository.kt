package io.schiar.fridgnet.model.repository

import io.schiar.fridgnet.model.datasource.AdministrativeUnitDataSource
import javax.inject.Inject

/**
 * The repository that exposes the administrative unit's screen flow. This repository manages
 * the data flow related to administrative units, providing access to the current state of
 * administrative units being displayed on the screen.
 */
class AdministrativeUnitRepository @Inject constructor(
    administrativeUnitDataSource: AdministrativeUnitDataSource
)  {
    /**
     * The current Flow of AdministrativeUnit that is being shown in the screen, each time a
     * Image is added, or a Region is switched (yield from being visible when uncheck of the
     * user) the Flow will automatically emit the most updated version of the current
     * AdministrativeUnit
     */
    val administrativeUnitFlow = administrativeUnitDataSource.retrieveCurrent()
}