package com.zarki.app.ui.reader

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zarki.app.ZarkiApplication
import com.zarki.app.data.settings.ReaderMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ReaderState(
    val loading: Boolean = true,
    val pages: List<String> = emptyList(),
    val error: String? = null,
)

class ReaderViewModel(private val chapterId: String) : ViewModel() {

    private val app = ZarkiApplication.instance
    private val source = app.sources.current
    private val settings = app.settings

    private val _state = MutableStateFlow(ReaderState())
    val state: StateFlow<ReaderState> = _state.asStateFlow()

    private val _mode = MutableStateFlow(settings.state.value.readerMode)
    val mode: StateFlow<ReaderMode> = _mode.asStateFlow()

    init {
        load()
    }

    fun setMode(mode: ReaderMode) {
        _mode.value = mode
        settings.setReaderMode(mode)
    }

    fun load() {
        _state.value = ReaderState(loading = true)
        viewModelScope.launch {
            runCatching { source.pages(chapterId) }
                .onSuccess { _state.value = ReaderState(loading = false, pages = it) }
                .onFailure { _state.value = ReaderState(loading = false, error = it.message ?: "Failed to load pages") }
        }
    }
}
