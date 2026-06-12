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

enum class BrowseFilter { POPULAR, LATEST }

data class CatalogState(
    val loading: Boolean = false,
    val manga: List<Manga> = emptyList(),
    val query: String = "",
    val filter: BrowseFilter = BrowseFilter.POPULAR,
    val sourceName: String = "",
    val error: String? = null,
)

/** Browses a single source's catalogue (popular / latest / search). */
class CatalogViewModel(sourceId: String) : ViewModel() {

    private val manager = ZarkiApplication.instance.sources
    private val source = manager.sources.firstOrNull { it.id == sourceId } ?: manager.current

    private val _state = MutableStateFlow(CatalogState(sourceName = source.name))
    val state: StateFlow<CatalogState> = _state.asStateFlow()

    private var searchJob: Job? = null

    init {
        load(BrowseFilter.POPULAR)
    }

    fun load(filter: BrowseFilter) {
        _state.value = _state.value.copy(loading = true, error = null, query = "", filter = filter)
        viewModelScope.launch {
            runCatching {
                when (filter) {
                    BrowseFilter.POPULAR -> source.popular()
                    BrowseFilter.LATEST -> source.latest()
                }
            }
                .onSuccess { _state.value = _state.value.copy(loading = false, manga = it) }
                .onFailure { _state.value = _state.value.copy(loading = false, error = it.message ?: "Failed to load") }
        }
    }

    fun onQueryChange(q: String) {
        _state.value = _state.value.copy(query = q)
        searchJob?.cancel()
        if (q.isBlank()) {
            load(_state.value.filter)
            return
        }
        searchJob = viewModelScope.launch {
            delay(350)
            _state.value = _state.value.copy(loading = true, error = null)
            runCatching { source.search(q) }
                .onSuccess { _state.value = _state.value.copy(loading = false, manga = it) }
                .onFailure { _state.value = _state.value.copy(loading = false, error = it.message ?: "Search failed") }
        }
    }
}
