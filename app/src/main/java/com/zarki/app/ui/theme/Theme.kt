package com.zarki.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Purple = Color(0xFF8B6DFF)
private val PurpleDark = Color(0xFF6750A4)
private val Accent = Color(0xFF22D3EE)

private val DarkColors = darkColorScheme(
    primary = Purple,
    secondary = Accent,
    background = Color(0xFF0E0E13),
    surface = Color(0xFF16161E),
    surfaceVariant = Color(0xFF22222C),
    onBackground = Color(0xFFECECF1),
    onSurface = Color(0xFFECECF1),
)

private val LightColors = lightColorScheme(
    primary = PurpleDark,
    secondary = Color(0xFF0E7490),
)

@Composable
fun ZarkiTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content,
    )
}
