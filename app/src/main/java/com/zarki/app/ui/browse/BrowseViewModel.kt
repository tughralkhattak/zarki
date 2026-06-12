package com.zarki.app.ui.browse

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zarki.app.ZarkiApplication
import com.zarki.app.domain.Manga
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class BrowseState(
    val loading: Boolean = false,
    val manga: List<Manga> = emptyList(),
    val query: String = "",
    val error: String? = null,
)

class BrowseViewModel : ViewModel() {

    private val repo = ZarkiApplication.instance.repository

    private val _state = MutableStateFlow(BrowseState())
    val state: StateFlow<BrowseState> = _state.asStateFlow()

    private var searchJob: Job? = null

    init {
        loadPopular()
    }

    fun loadPopular() {
        _state.value = _state.value.copy(loading = true, error = null, query = "")
        viewModelScope.launch {
            runCatching { repo.popular() }
                .onSuccess { _state.value = _state.value.copy(loading = false, manga = it) }
                .onFailure { _state.value = _state.value.copy(loading = false, error = it.message ?: "Failed to load") }
        }
    }

    fun onQueryChange(q: String) {
        _state.value = _state.value.copy(query = q)
        searchJob?.cancel()
        if (q.isBlank()) {
            loadPopular()
            return
        }
        searchJob = viewModelScope.launch {
            delay(350) // debounce
            _state.value = _state.value.copy(loading = true, error = null)
            runCatching { repo.search(q) }
                .onSuccess { _state.value = _state.value.copy(loading = false, manga = it) }
                .onFailure { _state.value = _state.value.copy(loading = false, error = it.message ?: "Search failed") }
        }
    }
}
