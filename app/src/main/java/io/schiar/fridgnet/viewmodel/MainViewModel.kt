package io.schiar.fridgnet.viewmodel

import androidx.lifecycle.ViewModel
import io.schiar.fridgnet.model.repository.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class MainViewModel(private val repository: Repository): ViewModel() {
    private var _databaseLoaded = MutableStateFlow(value = false)
    val databaseLoaded: StateFlow<Boolean> = _databaseLoaded

    suspend fun loadDatabase() {
        repository.loadDatabase(onDatabaseLoaded = ::onDatabaseLoaded)
    }

    private fun onDatabaseLoaded() {
        _databaseLoaded.update { true }
    }
    suspend fun addURIs(uris: List<String>) { repository.addURIs(uris = uris) }
}