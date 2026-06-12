package com.zarki.app.ui.backup

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.zarki.app.ZarkiApplication
import kotlinx.coroutines.launch

@Composable
fun BackupScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val backup = ZarkiApplication.instance.backup
    var status by remember { mutableStateOf("") }

    val createDoc = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/json"),
    ) { uri ->
        if (uri != null) scope.launch {
            runCatching {
                val text = backup.exportJson()
                context.contentResolver.openOutputStream(uri)?.use { it.write(text.toByteArray()) }
            }.onSuccess { status = "✓ Backup saved successfully." }
                .onFailure { status = "✕ Couldn't save backup: ${it.message}" }
        }
    }

    val openDoc = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument(),
    ) { uri ->
        if (uri != null) scope.launch {
            runCatching {
                val text = context.contentResolver.openInputStream(uri)
                    ?.bufferedReader()?.use { it.readText() } ?: error("empty")
                backup.importJson(text)
            }.onSuccess { status = "✓ Restored $it items." }
                .onFailure { status = "✕ Couldn't read that backup file." }
        }
    }

    Column(modifier = Modifier.padding(20.dp)) {
        Text(
            "Back up your library and reading history to a file you can keep safe " +
                "or move to another device. Everything stays on your device — nothing is uploaded.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(24.dp))

        Button(
            onClick = { createDoc.launch("zarki_backup.json") },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Icon(Icons.Default.Save, contentDescription = null)
            Spacer(Modifier.height(0.dp))
            Text("  Create backup")
        }
        Spacer(Modifier.height(12.dp))
        OutlinedButton(
            onClick = { openDoc.launch(arrayOf("application/json")) },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Icon(Icons.Default.Restore, contentDescription = null)
            Text("  Restore backup")
        }

        if (status.isNotBlank()) {
            Spacer(Modifier.height(20.dp))
            Text(status, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
        }
    }
}
