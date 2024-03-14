package io.schiar.fridgnet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.schiar.fridgnet.model.repository.AppRepository
import kotlinx.coroutines.launch

class AppViewModel(private val appRepository: AppRepository) : ViewModel() {
    fun addURIs(uris: List<String>) = viewModelScope.launch {
        appRepository.addURIs(uris = uris)
    }
}