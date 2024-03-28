package io.schiar.fridgnet.model.repository

import io.schiar.fridgnet.model.datasource.AdministrativeUnitDataSource
import javax.inject.Inject

class AdministrativeUnitRepository @Inject constructor(
    administrativeUnitDataSource: AdministrativeUnitDataSource
)  {
    val administrativeUnitFlow = administrativeUnitDataSource.retrieveCurrent()
}