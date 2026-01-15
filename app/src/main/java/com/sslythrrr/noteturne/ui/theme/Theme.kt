package com.sslythrrr.noteturne.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = BladeAccent,
    onPrimary = ObsidianBlack,
    primaryContainer = ObsidianGray,
    onPrimaryContainer = SilverLight,
    secondary = SilverMedium,
    onSecondary = ObsidianBlack,
    background = ObsidianBlack,
    onBackground = SilverLight,
    surface = ObsidianDark,
    onSurface = SilverLight,
    surfaceVariant = ObsidianGray,
    onSurfaceVariant = SilverMedium,
    error = ErrorRed,
    onError = ObsidianBlack
)

private val LightColorScheme = lightColorScheme(
    primary = BladeAccentLight,
    onPrimary = SilverWhite,
    primaryContainer = SilverPale,
    onPrimaryContainer = ObsidianText,
    secondary = SilverDark,
    onSecondary = SilverWhite,
    background = SilverWhite,
    onBackground = ObsidianText,
    surface = SilverWhite,
    onSurface = ObsidianText,
    surfaceVariant = SilverPale,
    onSurfaceVariant = ObsidianTextLight,
    error = ErrorRed,
    onError = SilverWhite
)

@Composable
fun NoteturneTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}