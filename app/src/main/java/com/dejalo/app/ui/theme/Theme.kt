package com.dejalo.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = DejaloColors.TealDeep,
    onPrimary = Color.White,
    primaryContainer = DejaloColors.Mist,
    onPrimaryContainer = DejaloColors.Navy,
    secondary = DejaloColors.Crisis,
    onSecondary = Color.White,
    secondaryContainer = DejaloColors.CrisisSoft,
    onSecondaryContainer = DejaloColors.Navy,
    tertiary = DejaloColors.Lime,
    onTertiary = DejaloColors.Navy,
    background = DejaloColors.Paper,
    onBackground = DejaloColors.Navy,
    surface = DejaloColors.Cloud,
    onSurface = DejaloColors.Navy,
    surfaceVariant = DejaloColors.Mist,
    onSurfaceVariant = DejaloColors.InkMuted,
    outline = DejaloColors.Line,
    error = DejaloColors.Crisis,
    onError = Color.White
)

data class DejaloExtendedColors(
    val brandGradientStart: Color = DejaloColors.Lime,
    val brandGradientEnd: Color = DejaloColors.Teal,
    val navy: Color = DejaloColors.Navy,
    val crisis: Color = DejaloColors.Crisis
)

val LocalDejaloColors = staticCompositionLocalOf { DejaloExtendedColors() }

@Composable
fun DejaloTheme(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalDejaloColors provides DejaloExtendedColors()) {
        MaterialTheme(
            colorScheme = LightColors,
            typography = DejaloTypography,
            content = content
        )
    }
}
