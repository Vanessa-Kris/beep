package com.example.beep.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = BluePrimary,
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = BluePrimaryContainer,
    onPrimaryContainer = Color(0xFF001F29),

    secondary = BlueSecondary,
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = BlueSecondaryContainer,
    onSecondaryContainer = Color(0xFF001F29),

    tertiary = BlueTertiary,
    onTertiary = Color(0xFFFFFFFF),

    background = LightBackground,
    onBackground = Color(0xFF1C1C1C),

    surface = LightSurface,
    onSurface = Color(0xFF1C1C1C),
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = Color(0xFF616161),

    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF)
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkBluePrimary,
    onPrimary = Color(0xFF003544),
    primaryContainer = DarkBluePrimaryContainer,
    onPrimaryContainer = Color(0xFFB3E5FC),

    secondary = DarkBlueSecondary,
    onSecondary = Color(0xFF003544),
    secondaryContainer = DarkBlueSecondaryContainer,
    onSecondaryContainer = Color(0xFFE1F5FE),

    tertiary = DarkBlueTertiary,
    onTertiary = Color(0xFF003544),

    background = DarkBackground,
    onBackground = Color(0xFFE0E0E0),

    surface = DarkBackground,  // Changed to use DarkBackground
    onSurface = Color(0xFFE0E0E0),
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = Color(0xFFBDBDBD),

    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005)
)


@Composable
fun BeepTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
