package com.zarki.app.ui.browse

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zarki.app.ZarkiApplication

@Composable
fun BrowseScreen(onOpenSource: (String) -> Unit) {
    val manager = ZarkiApplication.instance.sources

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            "Sources",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(16.dp, 18.dp, 16.dp, 4.dp),
        )
        manager.sources.forEach { source ->
            ListItem(
                headlineContent = { Text(source.name) },
                supportingContent = { Text("${source.lang.uppercase()} • tap to browse & read") },
                leadingContent = { Icon(Icons.Default.Public, contentDescription = null) },
                trailingContent = {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
                },
                modifier = Modifier.clickable { onOpenSource(source.id) },
            )
            HorizontalDivider()
        }
    }
}
