package com.zarki.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.zarki.app.data.settings.ThemeMode

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

private val AmoledColors = darkColorScheme(
    primary = Purple,
    secondary = Accent,
    background = Color(0xFF000000),
    surface = Color(0xFF000000),
    surfaceVariant = Color(0xFF15151B),
    onBackground = Color(0xFFECECF1),
    onSurface = Color(0xFFECECF1),
)

private val LightColors = lightColorScheme(
    primary = PurpleDark,
    secondary = Color(0xFF0E7490),
)

@Composable
fun ZarkiTheme(
    theme: ThemeMode = ThemeMode.DARK,
    content: @Composable () -> Unit,
) {
    val systemDark = isSystemInDarkTheme()
    val colors = when (theme) {
        ThemeMode.SYSTEM -> if (systemDark) DarkColors else LightColors
        ThemeMode.LIGHT -> LightColors
        ThemeMode.DARK -> DarkColors
        ThemeMode.AMOLED -> AmoledColors
    }
    MaterialTheme(colorScheme = colors, content = content)
}
