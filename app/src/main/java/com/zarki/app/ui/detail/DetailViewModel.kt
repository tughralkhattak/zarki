package com.zarki.app.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zarki.app.ZarkiApplication
import com.zarki.app.data.local.LibraryManga
import com.zarki.app.data.local.ReadProgress
import com.zarki.app.domain.Chapter
import com.zarki.app.domain.Manga
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class DetailState(
    val loading: Boolean = true,
    val manga: Manga? = null,
    val chapters: List<Chapter> = emptyList(),
    val ascending: Boolean = false,
    val error: String? = null,
)

class DetailViewModel(private val mangaId: String) : ViewModel() {

    private val app = ZarkiApplication.instance
    private val source = app.sources.current
    private val libDao = app.database.libraryDao()
    private val progressDao = app.database.progressDao()

    private val _state = MutableStateFlow(DetailState())
    val state: StateFlow<DetailState> = _state.asStateFlow()

    val isSaved: StateFlow<Boolean> = libDao.isSaved(mangaId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val readChapterIds: StateFlow<Set<String>> = progressDao.readChapterIds(mangaId)
        .map { it.toSet() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    val downloadedIds: StateFlow<Set<String>> = app.downloads.downloadedIds()
        .map { it.toSet() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    val activeDownloads: StateFlow<Set<String>> = app.downloads.active
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    fun toggleDownload(chapter: Chapter) {
        val manga = _state.value.manga ?: return
        viewModelScope.launch {
            if (chapter.id in downloadedIds.value) {
                app.downloads.delete(chapter.id)
            } else {
                app.downloads.download(chapter, manga.id, manga.title, manga.coverUrl)
            }
        }
    }

    init {
        load()
    }

    fun load() {
        _state.value = DetailState(loading = true)
        viewModelScope.launch {
            runCatching {
                val manga = source.details(mangaId)
                val chapters = source.chapters(mangaId)
                manga to chapters
            }.onSuccess { (manga, chapters) ->
                _state.value = DetailState(loading = false, manga = manga, chapters = chapters)
            }.onFailure {
                _state.value = DetailState(loading = false, error = it.message ?: "Failed to load")
            }
        }
    }

    fun toggleSort() {
        val s = _state.value
        _state.value = s.copy(ascending = !s.ascending, chapters = s.chapters.reversed())
    }

    /** The chapter to resume from: the last-read one, else the first in reading order. */
    suspend fun continueChapter(): Chapter? {
        val last = progressDao.lastRead(mangaId)
        val chapters = _state.value.chapters
        if (last != null) {
            chapters.firstOrNull { it.id == last.chapterId }?.let { return it }
        }
        return chapters.lastOrNull() // chapters are desc by default, so last = chapter 1
    }

    /** Record that a chapter was opened — marks it read and feeds the History tab. */
    fun markOpened(chapter: Chapter) {
        val manga = _state.value.manga ?: return
        viewModelScope.launch {
            progressDao.upsert(
                ReadProgress(
                    chapterId = chapter.id,
                    mangaId = manga.id,
                    mangaTitle = manga.title,
                    coverUrl = manga.coverUrl,
                    chapterNumber = chapter.number,
                    lastPage = 0,
                    totalPages = chapter.pages,
                    read = true,
                    updatedAt = System.currentTimeMillis(),
                ),
            )
        }
    }

    fun toggleLibrary() {
        val manga = _state.value.manga ?: return
        viewModelScope.launch {
            if (isSaved.value) {
                libDao.remove(manga.id)
            } else {
                libDao.add(
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
