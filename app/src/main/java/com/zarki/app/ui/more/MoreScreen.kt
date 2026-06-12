package com.zarki.app.ui.more

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MoreScreen(
    onOpenDownloads: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenAbout: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            "Zarki",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(16.dp, 24.dp, 16.dp, 8.dp),
        )
        HorizontalDivider()
        ListItem(
            headlineContent = { Text("Downloads") },
            supportingContent = { Text("Chapters saved for offline reading") },
            leadingContent = { Icon(Icons.Default.Download, contentDescription = null) },
            modifier = Modifier.clickable(onClick = onOpenDownloads),
        )
        ListItem(
            headlineContent = { Text("Settings") },
            supportingContent = { Text("Theme, reader mode & more") },
            leadingContent = { Icon(Icons.Default.Settings, contentDescription = null) },
            modifier = Modifier.clickable(onClick = onOpenSettings),
        )
        ListItem(
            headlineContent = { Text("About") },
            supportingContent = { Text("Author, version & app info") },
            leadingContent = { Icon(Icons.Default.Info, contentDescription = null) },
            modifier = Modifier.clickable(onClick = onOpenAbout),
        )
    }
}
