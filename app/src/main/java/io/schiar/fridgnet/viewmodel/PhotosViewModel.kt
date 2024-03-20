package io.schiar.fridgnet.viewmodel

import androidx.lifecycle.ViewModel
import io.schiar.fridgnet.model.repository.PhotosRepository
import io.schiar.fridgnet.viewmodel.util.toAdminUnitViewData
import kotlinx.coroutines.flow.map

class PhotosViewModel(photosRepository: PhotosRepository) : ViewModel() {
    val adminUnit = photosRepository.adminUnit.map { adminUnit -> adminUnit.toAdminUnitViewData() }
}