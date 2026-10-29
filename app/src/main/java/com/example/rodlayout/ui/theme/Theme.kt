package com.example.rodlayout.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = IndustrialOrange,
    onPrimary = Charcoal900,
    primaryContainer = IndustrialOrangeDark,
    onPrimaryContainer = SteelTextPrimary,
    secondary = AmberAccent,
    onSecondary = Charcoal900,
    background = Charcoal900,
    onBackground = SteelTextPrimary,
    surface = Charcoal800,
    onSurface = SteelTextPrimary,
    surfaceVariant = Charcoal700,
    onSurfaceVariant = SteelTextSecondary,
    outline = Charcoal600,
    outlineVariant = Charcoal500,
    error = StatusError,
    onError = PureWhite
)

private val LightColorScheme = lightColorScheme(
    primary = IndustrialOrange,
    onPrimary = PureWhite,
    primaryContainer = Color(0xFFFFECE0),
    onPrimaryContainer = IndustrialOrangeDark,
    secondary = AmberAccent,
    onSecondary = CharcoalTextPrimary,
    background = SurfaceLight,
    onBackground = CharcoalTextPrimary,
    surface = CardLight,
    onSurface = CharcoalTextPrimary,
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = CharcoalTextSecondary,
    outline = BorderLight,
    outlineVariant = Color(0xFFCBD5E1),
    error = StatusError,
    onError = PureWhite
)

@Composable
fun RodLayoutTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = colorScheme.background.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
