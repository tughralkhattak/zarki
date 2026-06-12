package com.zarki.app.ui.browse

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zarki.app.ZarkiApplication

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowseScreen(onOpenSource: (String) -> Unit) {
    val app = ZarkiApplication.instance
    val manager = app.sources
    val repos by app.settings.repos.collectAsStateWithLifecycle()
    var showAdd by remember { mutableStateOf(false) }
    var repoUrl by remember { mutableStateOf("") }

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

        Text(
            "Extension repositories",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(16.dp, 22.dp, 16.dp, 4.dp),
        )
        if (repos.isEmpty()) {
            Text(
                "No repositories added. Zarki ships with the legal MangaDex source. " +
                    "You can add your own extension-repo URLs — you're responsible for what you add.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(16.dp, 4.dp),
            )
        } else {
            repos.forEach { url ->
                ListItem(headlineContent = { Text(url, maxLines = 1) })
            }
        }
        ListItem(
            headlineContent = { Text("Add repository") },
            leadingContent = { Icon(Icons.Default.Add, contentDescription = null) },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showAdd = true },
        )
    }

    if (showAdd) {
        AlertDialog(
            onDismissRequest = { showAdd = false },
            title = { Text("Add extension repository") },
            text = {
                Column {
                    OutlinedTextField(
                        value = repoUrl,
                        onValueChange = { repoUrl = it },
                        label = { Text("Repository URL") },
                        placeholder = { Text("https://…/index.json") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (repoUrl.isNotBlank()) app.settings.addRepo(repoUrl.trim())
                    repoUrl = ""
                    showAdd = false
                }) { Text("Save") }
            },
            dismissButton = { TextButton(onClick = { showAdd = false }) { Text("Cancel") } },
        )
    }
}
