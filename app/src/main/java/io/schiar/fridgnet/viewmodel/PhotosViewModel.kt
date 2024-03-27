package io.schiar.fridgnet.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.schiar.fridgnet.model.repository.PhotosRepository
import io.schiar.fridgnet.viewmodel.util.toAdministrativeUnitViewData
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class PhotosViewModel @Inject constructor(photosRepository: PhotosRepository) : ViewModel() {
    val administrativeUnit = photosRepository.administrativeUnit.map {
        administrativeUnit -> administrativeUnit.toAdministrativeUnitViewData()
    }
}