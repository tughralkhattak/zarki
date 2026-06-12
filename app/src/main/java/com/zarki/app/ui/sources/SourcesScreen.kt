package com.zarki.app.ui.sources

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.zarki.app.ZarkiApplication

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SourcesScreen() {
    val manager = ZarkiApplication.instance.sources
    var showAdd by remember { mutableStateOf(false) }
    var repoUrl by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            "Installed sources",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(16.dp, 16.dp, 16.dp, 4.dp),
        )
        manager.sources.forEach { source ->
            ListItem(
                headlineContent = { Text(source.name) },
                supportingContent = { Text("Language: ${source.lang.uppercase()} • Official API") },
                leadingContent = { Icon(Icons.Default.Public, contentDescription = null) },
                trailingContent = {
                    if (source.enabled) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = "Enabled",
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                },
            )
        }

        Text(
            "Extension repositories",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(16.dp, 24.dp, 16.dp, 4.dp),
        )
        ListItem(
            headlineContent = { Text("Add a repository") },
            supportingContent = { Text("Paste an extension-repo URL to add more sources") },
            leadingContent = { Icon(Icons.Default.Add, contentDescription = null) },
            modifier = Modifier.fillMaxWidth().padding(0.dp),
        )
        Text(
            "Zarki ships with the legal MangaDex source. You can add your own " +
                "extension repositories here; you are responsible for the content of any repo you add.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(16.dp, 8.dp),
        )
        TextButton(onClick = { showAdd = true }, modifier = Modifier.padding(start = 8.dp)) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(Modifier.width(6.dp))
            Text("Add repository URL")
        }
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
                    Spacer(Modifier.size(8.dp))
                    Text(
                        "Remote extension loading lands in a future update — this saves the URL for now.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            },
            confirmButton = { TextButton(onClick = { showAdd = false }) { Text("Save") } },
            dismissButton = { TextButton(onClick = { showAdd = false }) { Text("Cancel") } },
        )
    }
}
