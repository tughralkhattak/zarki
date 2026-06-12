package com.zarki.app.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.zarki.app.domain.Chapter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    viewModel: DetailViewModel,
    onBack: () -> Unit,
    onOpenChapter: (chapterId: String, title: String) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val saved by viewModel.isSaved.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.manga?.title ?: "Details", maxLines = 1) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (state.manga != null) {
                        IconButton(onClick = viewModel::toggleLibrary) {
                            Icon(
                                if (saved) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Toggle library",
                                tint = if (saved) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurface,
                            )
                        }
                    }
                },
            )
        },
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            when {
                state.loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                state.error != null -> Text(
                    "⚠ ${state.error}",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center).padding(24.dp),
                )
                state.manga != null -> LazyColumn(
                    contentPadding = PaddingValues(bottom = 24.dp),
                    modifier = Modifier.fillMaxSize(),
                ) {
                    item { Header(viewModel) }
                    item {
                        Text(
                            "Chapters (${state.chapters.size})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(16.dp, 8.dp),
                        )
                    }
                    items(state.chapters, key = { it.id }) { ch ->
                        ChapterRow(ch) { onOpenChapter(ch.id, ch.display) }
                    }
                }
            }
        }
    }
}

@Composable
private fun Header(viewModel: DetailViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val manga = state.manga ?: return
    Column {
        Row(modifier = Modifier.padding(16.dp)) {
            AsyncImage(
                model = manga.coverUrl,
                contentDescription = manga.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(120.dp)
                    .height(176.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
            )
            Spacer(Modifier.width(16.dp))
            Column {
                Text(manga.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                if (manga.authors.isNotEmpty()) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        manga.authors.joinToString(", "),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    buildString {
                        if (manga.status.isNotBlank()) append(manga.status)
                        if (manga.year != null) append("  •  ${manga.year}")
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
        if (manga.description.isNotBlank()) {
            Text(
                manga.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
        }
    }
}

@Composable
private fun ChapterRow(chapter: Chapter, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp, 14.dp),
    ) {
        Text(chapter.display, style = MaterialTheme.typography.bodyLarge, maxLines = 1)
        if (chapter.pages > 0) {
            Text(
                "${chapter.pages}p",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
