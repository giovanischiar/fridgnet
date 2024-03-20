package io.schiar.fridgnet.viewmodel

import androidx.lifecycle.ViewModel
import io.schiar.fridgnet.model.repository.PhotosRepository
import io.schiar.fridgnet.viewmodel.util.toAdministrativeUnitViewData
import kotlinx.coroutines.flow.map

class PhotosViewModel(photosRepository: PhotosRepository) : ViewModel() {
    val administrativeUnit = photosRepository.administrativeUnit.map { administrativeUnit -> administrativeUnit.toAdministrativeUnitViewData() }
}