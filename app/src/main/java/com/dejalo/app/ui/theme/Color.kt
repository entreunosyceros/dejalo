package com.dejalo.app.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/** Paleta anclada al logo DÉJALO! (lima → teal, navy). */
object DejaloColors {
    val Lime = Color(0xFFB4E04A)
    val Teal = Color(0xFF00C2B0)
    val TealDeep = Color(0xFF0A8F86)
    val Navy = Color(0xFF14245C)
    val NavySoft = Color(0xFF2A3A7A)
    val Mist = Color(0xFFEFFAF7)
    val Paper = Color(0xFFF7FFFC)
    val Cloud = Color(0xFFFFFFFF)
    val Crisis = Color(0xFFE85D4C)
    val CrisisSoft = Color(0xFFFFE8E4)
    val InkMuted = Color(0xFF4A5578)
    val Line = Color(0xFFD5EBE6)

    val BrandGradient = Brush.linearGradient(
        colors = listOf(Lime, Teal)
    )

    val ScreenGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFE8FFF6),
            Mist,
            Paper
        )
    )

    val HeroGlow = Brush.radialGradient(
        colors = listOf(
            Teal.copy(alpha = 0.22f),
            Lime.copy(alpha = 0.10f),
            Color.Transparent
        )
    )

    val CrisisGradient = Brush.verticalGradient(
        colors = listOf(
            CrisisSoft,
            Paper
        )
    )
}
