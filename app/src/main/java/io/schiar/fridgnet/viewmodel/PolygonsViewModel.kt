package io.schiar.fridgnet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.schiar.fridgnet.model.repository.PolygonsRepository
import io.schiar.fridgnet.viewmodel.util.toCartographicBoundaryViewData
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PolygonsViewModel @Inject constructor(
    private val polygonsRepository: PolygonsRepository
) : ViewModel() {
    val currentCartographicBoundary = polygonsRepository
        .currentCartographicBoundary
        .map { it?.toCartographicBoundaryViewData() }

    fun switchRegionAt(index: Int) = viewModelScope.launch {
        polygonsRepository.switchRegionAt(index = index)
    }

    fun switchAll() = viewModelScope.launch { polygonsRepository.switchAll() }
}