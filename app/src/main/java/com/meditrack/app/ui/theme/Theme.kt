package com.meditrack.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Clean light color scheme with teal primary and green secondary
private val MediTrackColorScheme = lightColorScheme(
    primary = TealPrimary,
    onPrimary = OnTeal,
    primaryContainer = Color(0xFFB2DFDB),
    onPrimaryContainer = Color(0xFF004D40),
    secondary = GreenSecondary,
    onSecondary = OnTeal,
    secondaryContainer = Color(0xFFC8E6C9),
    onSecondaryContainer = Color(0xFF1B5E20),
    tertiary = TealLight,
    background = WhiteBackground,
    onBackground = OnWhite,
    surface = WhiteBackground,
    onSurface = OnWhite,
    surfaceVariant = LightGray,
    onSurfaceVariant = MediumGray,
    error = ErrorRed,
    onError = OnTeal,
    outline = MediumGray,
    outlineVariant = Color(0xFFCFD8DC)
)

@Composable
fun MediTrackTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = MediTrackColorScheme,
        typography = MediTrackTypography,
        content = content
    )
}
