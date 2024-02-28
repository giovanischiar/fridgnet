package io.schiar.fridgnet.viewmodel

import androidx.lifecycle.ViewModel
import io.schiar.fridgnet.model.repository.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class AppViewModel(private val appRepository: AppRepository) : ViewModel() {
    private var _databaseLoaded = MutableStateFlow(value = false)
    val databaseLoaded: StateFlow<Boolean> = _databaseLoaded

    suspend fun loadDatabase() {
        appRepository.loadDatabase(onDatabaseLoaded = ::onDatabaseLoaded)
    }

    private fun onDatabaseLoaded() {
        _databaseLoaded.update { true }
    }

    suspend fun addURIs(uris: List<String>) {
        appRepository.addURIs(uris = uris)
    }
}