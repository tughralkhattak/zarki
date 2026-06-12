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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.zarki.app.domain.Chapter
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    viewModel: DetailViewModel,
    onBack: () -> Unit,
    onOpenChapter: (chapterId: String, title: String) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val saved by viewModel.isSaved.collectAsStateWithLifecycle()
    val readIds by viewModel.readChapterIds.collectAsStateWithLifecycle()
    val downloadedIds by viewModel.downloadedIds.collectAsStateWithLifecycle()
    val activeDownloads by viewModel.activeDownloads.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val open: (Chapter) -> Unit = { ch ->
        viewModel.markOpened(ch)
        onOpenChapter(ch.id, ch.display)
    }
    val context = LocalContext.current
    fun mangaUrl() = "https://mangadex.org/title/${state.manga?.id.orEmpty()}"

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
                        IconButton(onClick = {
                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(mangaUrl())))
                        }) {
                            Icon(Icons.Default.OpenInNew, contentDescription = "Open in browser")
                        }
                        IconButton(onClick = {
                            val send = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, "${state.manga?.title}\n${mangaUrl()}")
                            }
                            context.startActivity(Intent.createChooser(send, "Share manga"))
                        }) {
                            Icon(Icons.Default.Share, contentDescription = "Share")
                        }
                        IconButton(onClick = viewModel::toggleSort) {
                            Icon(Icons.AutoMirrored.Filled.Sort, contentDescription = "Sort chapters")
                        }
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
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
            )
        },
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            when {
                state.loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                state.error != null -> Text(
                    "⚠ ${state.error}",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(24.dp),
                )
                state.manga != null -> LazyColumn(
                    contentPadding = PaddingValues(bottom = 24.dp),
                    modifier = Modifier.fillMaxSize(),
                ) {
                    item {
                        Header(
                            viewModel = viewModel,
                            topPadding = padding.calculateTopPadding(),
                            onContinue = {
                                scope.launch {
                                    viewModel.continueChapter()?.let { open(it) }
                                }
                            },
                        )
                    }
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp, 12.dp, 16.dp, 8.dp),
                        ) {
                            Text(
                                "Chapters (${state.chapters.size})",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                            )
                            Text(
                                if (state.ascending) "Oldest first" else "Newest first",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                    items(state.chapters, key = { it.id }) { ch ->
                        ChapterRow(
                            chapter = ch,
                            read = ch.id in readIds,
                            downloaded = ch.id in downloadedIds,
                            downloading = ch.id in activeDownloads,
                            onClick = { open(ch) },
                            onDownload = { viewModel.toggleDownload(ch) },
                        )
                    }
                }
                else -> Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(24.dp),
                ) {
                    Text("Couldn't load this manga.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(12.dp))
                    Button(onClick = viewModel::load) { Text("Retry") }
                }
            }
        }
    }
}

@Composable
private fun Header(viewModel: DetailViewModel, topPadding: androidx.compose.ui.unit.Dp, onContinue: () -> Unit) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val manga = state.manga ?: return

    Column {
        // backdrop: cover image faded into the background for a premium header
        Box(modifier = Modifier.fillMaxWidth()) {
            AsyncImage(
                model = manga.coverUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp),
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .background(
                        Brush.verticalGradient(
                            0f to Color(0x66000000),
                            1f to MaterialTheme.colorScheme.background,
                        ),
                    ),
            )
            Row(modifier = Modifier.padding(16.dp, topPadding + 48.dp, 16.dp, 0.dp)) {
                AsyncImage(
                    model = manga.coverUrl,
                    contentDescription = manga.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .width(120.dp)
                        .height(172.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                )
                Spacer(Modifier.width(16.dp))
                Column(modifier = Modifier.align(Alignment.Bottom)) {
                    Text(
                        manga.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        maxLines = 3,
                    )
                    if (manga.authors.isNotEmpty()) {
                        Spacer(Modifier.height(4.dp))
                        Text(
                            manga.authors.joinToString(", "),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Spacer(Modifier.height(6.dp))
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
        }

        // action buttons
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(16.dp, 12.dp),
        ) {
            Button(onClick = onContinue, modifier = Modifier.weight(1f)) {
                Icon(Icons.Default.PlayArrow, contentDescription = null)
                Spacer(Modifier.width(6.dp))
                Text("Read")
            }
            OutlinedButton(onClick = viewModel::toggleLibrary, modifier = Modifier.weight(1f)) {
                Text("♡ Library")
            }
        }

        // tags
        if (manga.tags.isNotEmpty()) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
            ) {
                items(manga.tags) { tag ->
                    AssistChip(onClick = {}, label = { Text(tag) })
                }
            }
        }

        if (manga.description.isNotBlank()) {
            Text(
                manga.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(16.dp, 12.dp),
            )
        }
    }
}

@Composable
private fun ChapterRow(
    chapter: Chapter,
    read: Boolean,
    downloaded: Boolean,
    downloading: Boolean,
    onClick: () -> Unit,
    onDownload: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(start = 16.dp, top = 6.dp, bottom = 6.dp, end = 4.dp),
    ) {
        if (read) {
            Icon(
                Icons.Default.CheckCircle,
                contentDescription = "Read",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.width(20.dp),
            )
            Spacer(Modifier.width(10.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                chapter.display,
                style = MaterialTheme.typography.bodyLarge,
                color = if (read) MaterialTheme.colorScheme.onSurfaceVariant
                else MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
            )
            if (chapter.pages > 0) {
                Text(
                    "${chapter.pages} pages" + if (downloaded) " • saved offline" else "",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        IconButton(onClick = onDownload) {
            when {
                downloading -> CircularProgressIndicator(
                    strokeWidth = 2.dp,
                    modifier = Modifier.width(20.dp),
                )
                downloaded -> Icon(
                    Icons.Default.DownloadDone,
                    contentDescription = "Downloaded (tap to remove)",
                    tint = MaterialTheme.colorScheme.primary,
                )
                else -> Icon(
                    Icons.Default.Download,
                    contentDescription = "Download for offline",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
