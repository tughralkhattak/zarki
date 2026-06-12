package com.zarki.app.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zarki.app.ZarkiApplication
import com.zarki.app.data.settings.ReaderMode
import com.zarki.app.data.settings.ThemeMode

@Composable
fun SettingsScreen() {
    val store = ZarkiApplication.instance.settings
    val settings by store.state.collectAsStateWithLifecycle()

    Column(modifier = Modifier.padding(top = 8.dp)) {
        SectionTitle("Appearance")
        ThemeMode.entries.forEach { mode ->
            OptionRow(
                label = when (mode) {
                    ThemeMode.SYSTEM -> "Follow system"
                    ThemeMode.LIGHT -> "Light"
                    ThemeMode.DARK -> "Dark"
                    ThemeMode.AMOLED -> "AMOLED black"
                },
                selected = settings.theme == mode,
                onClick = { store.setTheme(mode) },
            )
        }

        SectionTitle("Default reader mode")
        ReaderMode.entries.forEach { mode ->
            OptionRow(
                label = mode.label,
                selected = settings.readerMode == mode,
                onClick = { store.setReaderMode(mode) },
            )
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(16.dp, 18.dp, 16.dp, 4.dp),
    )
}

@Composable
private fun OptionRow(label: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .selectable(selected = selected, onClick = onClick)
            .padding(16.dp, 12.dp),
    ) {
        RadioButton(selected = selected, onClick = onClick)
        Text(label, style = MaterialTheme.typography.bodyLarge)
    }
}
