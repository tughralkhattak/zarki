package com.zarki.app.data.settings

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class ThemeMode { SYSTEM, LIGHT, DARK, AMOLED }

enum class ReaderMode {
    WEBTOON,   // continuous vertical (default for webtoons)
    PAGED,     // horizontal swipe, left-to-right
    PAGED_RTL; // horizontal swipe, right-to-left (manga-style)

    val label: String
        get() = when (this) {
            WEBTOON -> "Webtoon (vertical)"
            PAGED -> "Paged (L→R)"
            PAGED_RTL -> "Paged (R→L, manga)"
        }
}

enum class LibrarySort { RECENT, TITLE }

data class Settings(
    val theme: ThemeMode = ThemeMode.DARK,
    val readerMode: ReaderMode = ReaderMode.WEBTOON,
    val librarySort: LibrarySort = LibrarySort.RECENT,
)

/**
 * Simple, reactive settings backed by SharedPreferences. Exposes a StateFlow so
 * Compose recomposes instantly when a setting changes (e.g. theme switch).
 */
class SettingsStore(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("zarki_settings", Context.MODE_PRIVATE)

    private val _state = MutableStateFlow(load())
    val state: StateFlow<Settings> = _state.asStateFlow()

    private fun load() = Settings(
        theme = ThemeMode.valueOf(prefs.getString("theme", ThemeMode.DARK.name)!!),
        readerMode = ReaderMode.valueOf(prefs.getString("reader", ReaderMode.WEBTOON.name)!!),
        librarySort = LibrarySort.valueOf(prefs.getString("sort", LibrarySort.RECENT.name)!!),
    )

    fun setTheme(mode: ThemeMode) = update { it.copy(theme = mode) }.also {
        prefs.edit().putString("theme", mode.name).apply()
    }

    fun setReaderMode(mode: ReaderMode) = update { it.copy(readerMode = mode) }.also {
        prefs.edit().putString("reader", mode.name).apply()
    }

    fun setLibrarySort(sort: LibrarySort) = update { it.copy(librarySort = sort) }.also {
        prefs.edit().putString("sort", sort.name).apply()
    }

    private inline fun update(block: (Settings) -> Settings) {
        _state.value = block(_state.value)
    }
}
