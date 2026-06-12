package com.zarki.app.ui.reader

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zarki.app.ZarkiApplication
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

    private val repo = ZarkiApplication.instance.repository

    private val _state = MutableStateFlow(ReaderState())
    val state: StateFlow<ReaderState> = _state.asStateFlow()

    init {
        load()
    }

    fun load() {
        _state.value = ReaderState(loading = true)
        viewModelScope.launch {
            runCatching { repo.pages(chapterId) }
                .onSuccess { _state.value = ReaderState(loading = false, pages = it) }
                .onFailure { _state.value = ReaderState(loading = false, error = it.message ?: "Failed to load pages") }
        }
    }
}
