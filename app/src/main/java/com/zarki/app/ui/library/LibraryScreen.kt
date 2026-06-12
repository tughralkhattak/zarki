package com.zarki.app.ui.library

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zarki.app.ZarkiApplication
import com.zarki.app.domain.Manga
import com.zarki.app.ui.components.MangaCard
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import androidx.lifecycle.viewModelScope

class LibraryViewModel : ViewModel() {
    private val dao = ZarkiApplication.instance.database.libraryDao()

    val items: StateFlow<List<Manga>> = dao.observeAll()
        .map { list ->
            list.map { Manga(id = it.id, title = it.title, coverUrl = it.coverUrl) }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}

@Composable
fun LibraryScreen(
    onOpenManga: (String) -> Unit,
    viewModel: LibraryViewModel = viewModel(),
) {
    val items by viewModel.items.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {
        if (items.isEmpty()) {
            Text(
                "Your library is empty.\nTap the ♡ on any manga to save it here.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.Center),
            )
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(112.dp),
                contentPadding = PaddingValues(12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize(),
            ) {
                items(items, key = { it.id }) { manga ->
                    MangaCard(manga = manga, onClick = { onOpenManga(manga.id) })
                }
            }
        }
    }
}
