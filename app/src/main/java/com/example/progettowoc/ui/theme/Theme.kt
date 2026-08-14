package com.example.progettowoc.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    background = Color.Black,
    onBackground = Color.White,

    onSecondaryContainer = Color.Black,

    surface = Color.DarkGray,
    onSurface = Color.White,

    surfaceVariant = Color.DarkGray,
    onSurfaceVariant = Color.White,

    primary = LightGreen
)

private val LightColorScheme = lightColorScheme(
    background = LightLightGray,
    onBackground = Color.Black,

    onSecondaryContainer = Color.White,

    surface = Color.White,
    onSurface = Color.LightGray,

    surfaceVariant = Color.White,
    onSurfaceVariant = Color.Black,

    primary = LightGreen
)

@Composable
fun ProgettoWOCTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}