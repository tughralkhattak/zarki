package com.zarki.app.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zarki.app.ZarkiApplication
import com.zarki.app.data.local.LibraryManga
import com.zarki.app.domain.Chapter
import com.zarki.app.domain.Manga
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class DetailState(
    val loading: Boolean = true,
    val manga: Manga? = null,
    val chapters: List<Chapter> = emptyList(),
    val error: String? = null,
)

class DetailViewModel(private val mangaId: String) : ViewModel() {

    private val app = ZarkiApplication.instance
    private val repo = app.repository
    private val dao = app.database.libraryDao()

    private val _state = MutableStateFlow(DetailState())
    val state: StateFlow<DetailState> = _state.asStateFlow()

    val isSaved: StateFlow<Boolean> = dao.isSaved(mangaId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    init {
        load()
    }

    fun load() {
        _state.value = DetailState(loading = true)
        viewModelScope.launch {
            runCatching {
                val manga = repo.details(mangaId)
                val chapters = repo.chapters(mangaId)
                manga to chapters
            }.onSuccess { (manga, chapters) ->
                _state.value = DetailState(loading = false, manga = manga, chapters = chapters)
            }.onFailure {
                _state.value = DetailState(loading = false, error = it.message ?: "Failed to load")
            }
        }
    }

    fun toggleLibrary() {
        val manga = _state.value.manga ?: return
        viewModelScope.launch {
            if (isSaved.value) {
                dao.remove(manga.id)
            } else {
                dao.add(
                    LibraryManga(
                        id = manga.id,
                        title = manga.title,
                        coverUrl = manga.coverUrl,
                        addedAt = System.currentTimeMillis(),
                    ),
                )
            }
        }
    }
}
