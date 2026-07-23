package com.zarki.app.ui.about

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AboutScreen() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
    ) {
        Text(
            "Zarki",
            style = TextStyle(
                fontSize = 44.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp,
                brush = Brush.linearGradient(
                    listOf(Color(0xFFB39DFF), Color(0xFF8B6DFF), Color(0xFF22D3EE)),
                ),
            ),
            modifier = Modifier.padding(top = 16.dp),
        )
        Text(
            "Version 1.0",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Section("What is Zarki?")
        Body(
            "Zarki is a fast, modern, beautifully designed manga reader for Android. " +
                "Browse and search a huge legal catalogue, read in your preferred mode " +
                "(webtoon, left-to-right, or right-to-left manga style), save favourites to your " +
                "library, track your history, and download chapters to read offline — anywhere, " +
                "any time, with no clutter and no ads.",
        )

        Section("Private by design")
        Body(
            "🚫  No ads — ever\n" +
                "🚫  No trackers, no analytics\n" +
                "🔒  Your library, history & downloads never leave your device\n" +
                "🪶  Tiny and fast — a fraction of the size of other manga apps\n" +
                "📡  No account or sign-up required",
        )

        Section("Why it exists")
        Body(
            "Most manga apps are either bloated, full of ads, or send your data to the cloud. " +
                "Zarki was built to be the opposite: clean, private, lightning-fast, and a genuine " +
                "pleasure to read on. It focuses on doing the core experience — reading manga — " +
                "better than anything else.",
        )

        Section("Author")
        Body("Designed and built by Zarki.")

        Section("Rights & ownership")
        Body(
            "© 2026 Zarki. All rights reserved. Zarki and its source code are the " +
                "property of the author. Manga content is provided by the MangaDex API and remains " +
                "the property of its respective creators and rights holders.",
        )

        Body(
            "\nMade with ❤ using Kotlin & Jetpack Compose.",
        )
    }
}

@Composable
private fun Section(title: String) {
    Text(
        title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 22.dp, bottom = 6.dp),
    )
}

@Composable
private fun Body(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        lineHeight = 22.sp,
    )
}
