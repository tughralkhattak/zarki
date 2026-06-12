package com.zarki.app.ui.reader

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.SubcomposeAsyncImage
import com.zarki.app.data.settings.ReaderMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderScreen(
    viewModel: ReaderViewModel,
    title: String,
    onBack: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val mode by viewModel.mode.collectAsStateWithLifecycle()
    var menuVisible by remember { mutableStateOf(true) }
    val pagerState = rememberPagerState(pageCount = { state.pages.size })
    val toggleMenu = { menuVisible = !menuVisible }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
    ) {
        when {
            state.loading -> CircularProgressIndicator(
                Modifier.align(Alignment.Center),
                color = Color.White,
            )
            state.error != null -> Text(
                "⚠ ${state.error}\n\nTap back and try another chapter.",
                color = Color(0xFFFF8A8A),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(24.dp),
            )
            state.pages.isEmpty() -> Text(
                "No readable pages found for this chapter.\n" +
                    "It may be hosted on an external site (common on MangaDex). Try another chapter.",
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(24.dp),
            )
            else -> when (mode) {
                ReaderMode.WEBTOON -> WebtoonReader(state.pages, onTap = toggleMenu)
                ReaderMode.PAGED, ReaderMode.PAGED_RTL -> HorizontalPager(
                    state = pagerState,
                    reverseLayout = mode == ReaderMode.PAGED_RTL,
                    modifier = Modifier.fillMaxSize(),
                ) { page ->
                    ZoomableImage(url = state.pages[page], onTap = toggleMenu)
                }
            }
        }

        AnimatedVisibility(visible = menuVisible, modifier = Modifier.align(Alignment.TopCenter)) {
            TopAppBar(
                title = { Text(title, maxLines = 1, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xDD000000)),
            )
        }

        AnimatedVisibility(visible = menuVisible, modifier = Modifier.align(Alignment.BottomCenter)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xDD000000))
                    .padding(12.dp),
            ) {
                val total = state.pages.size
                val shown = if (mode == ReaderMode.WEBTOON) total else (pagerState.currentPage + 1).coerceAtMost(total)
                Text(
                    if (mode == ReaderMode.WEBTOON) "$total pages" else "$shown / $total",
                    color = Color.White,
                    style = MaterialTheme.typography.labelLarge,
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp),
                ) {
                    ReaderMode.entries.forEach { m ->
                        val selected = m == mode
                        Text(
                            text = when (m) {
                                ReaderMode.WEBTOON -> "Webtoon"
                                ReaderMode.PAGED -> "L→R"
                                ReaderMode.PAGED_RTL -> "R→L"
                            },
                            color = if (selected) MaterialTheme.colorScheme.primary else Color(0xFFBBBBBB),
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.pointerInput(m) { detectTapGestures { viewModel.setMode(m) } },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WebtoonReader(pages: List<String>, onTap: () -> Unit) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) { detectTapGestures { onTap() } },
    ) {
        itemsIndexed(pages) { index, url ->
            PageImage(url = url, index = index, fillHeight = false)
        }
    }
}

@Composable
private fun ZoomableImage(url: String, onTap: () -> Unit) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) { detectTapGestures(onTap = { onTap() }) }
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    scale = (scale * zoom).coerceIn(1f, 5f)
                    offset = if (scale > 1f) offset + pan else Offset.Zero
                }
            },
    ) {
        SubcomposeAsyncImage(
            model = url,
            contentDescription = null,
            contentScale = ContentScale.Fit,
            loading = { LoadingBox() },
            error = { ErrorBox() },
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offset.x,
                    translationY = offset.y,
                ),
        )
    }
}

@Composable
private fun PageImage(url: String, index: Int, fillHeight: Boolean) {
    SubcomposeAsyncImage(
        model = url,
        contentDescription = "Page ${index + 1}",
        contentScale = ContentScale.FillWidth,
        loading = { LoadingBox() },
        error = { ErrorBox() },
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
private fun LoadingBox() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(420.dp)
            .background(Color(0xFF111111)),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(color = Color.White)
    }
}

@Composable
private fun ErrorBox() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .background(Color(0xFF1A1A1A)),
        contentAlignment = Alignment.Center,
    ) {
        Icon(Icons.Default.BrokenImage, contentDescription = "Failed to load", tint = Color(0xFF888888))
    }
}
