package io.schiar.fridgnet.model.repository

import io.schiar.fridgnet.model.datasource.AdministrativeUnitDataSource
import javax.inject.Inject

class PhotosRepository @Inject constructor(
    administrativeUnitDataSource: AdministrativeUnitDataSource
)  {
    val administrativeUnit = administrativeUnitDataSource.retrieveCurrent()
}