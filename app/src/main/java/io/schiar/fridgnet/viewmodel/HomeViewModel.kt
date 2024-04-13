package io.schiar.fridgnet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.schiar.fridgnet.model.repository.HomeRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * The HomeViewModel is the point of connection between the Home screen and its Repository
 */
@HiltViewModel
class HomeViewModel @Inject constructor(private val homeRepository: HomeRepository) : ViewModel() {
    /**
     * Delegates the Repository to create the images the model based on the provided uris. It
     * creates a coroutine to do that.
     *
     * @param uris the uris of the images.
     */
    fun addURIs(uris: List<String>) = viewModelScope.launch {
        homeRepository.addURIs(uris = uris)
    }
}