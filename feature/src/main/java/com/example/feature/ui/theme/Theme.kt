package com.example.feature.ui.theme


import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = ToolbarBlue,
    onPrimary = Color.White,
    secondary = Brown,
    onSecondary = Color.White,
    background = Color(0xFFF3F3F3),
    onBackground = Color(0xFF000000),
    surface = Color.White,
    onSurface = Color(0xFF000000)
)

private val DarkColors = darkColorScheme(
    primary = ToolbarBlue,
    onPrimary = Color.White,
    secondary = Brown,
    onSecondary = Color.White,
    background = Color(0xFF121212),
    onBackground = Color(0xFFEDEDED),
    surface = Color(0xFF1E1E1E),
    onSurface = Color(0xFFEDEDED)
)

@Composable
fun NewsAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colors,
        typography = Typography(),
        content = content
    )
}