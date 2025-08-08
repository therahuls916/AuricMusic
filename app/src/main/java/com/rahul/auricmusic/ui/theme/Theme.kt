package com.rahul.auricmusic.ui.theme

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
private val DarkColorScheme = darkColorScheme(
    primary = Goldenrod,
    onPrimary = Black,
    background = DeepCharcoal,      // Use the darkest color for the main background
    onBackground = White,
    surface = RaisinBlack,          // Use this for elevated cards
    onSurface = White,
    surfaceVariant = OffBlack,      // Use this for secondary surfaces like the Mini-Player
    onSurfaceVariant = LightKhaki
)

private val LightColorScheme = lightColorScheme(
    primary = Goldenrod,                  // The main brand accent color remains the same.
    onPrimary = White,                     // Text on top of the golden buttons.
    background = Color(0xFFF9F9F9),       // A very light, clean, off-white background.
    onBackground = AlmostBlack,            // The darkest text color for high contrast.
    surface = White,                       // Cards will be pure white for a clean, elevated look.
    onSurface = AlmostBlack,               // Text on top of cards.
    surfaceVariant = Color(0xFFE7E0EC),   // A subtle gray for secondary surfaces like the Mini-Player.
    onSurfaceVariant = OuterSpaceGray      // Secondary text color.
)
@Composable
fun AuricMusicTheme(
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
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // Assumes a Typography.kt file exists
        content = content
    )
}