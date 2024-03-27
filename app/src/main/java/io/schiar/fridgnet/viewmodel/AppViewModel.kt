package io.schiar.fridgnet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.schiar.fridgnet.model.repository.AppRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(private val appRepository: AppRepository) : ViewModel() {
    fun addURIs(uris: List<String>) = viewModelScope.launch {
        appRepository.addURIs(uris = uris)
    }
}