package com.dejalo.app.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.dejalo.app.ui.theme.DejaloColors

@Composable
fun DejaloBackground(
    crisis: Boolean = false,
    content: @Composable BoxScope.() -> Unit
) {
    val pulse = rememberInfiniteTransition(label = "bgPulse")
    val shift by pulse.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(9000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shift"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(if (crisis) DejaloColors.CrisisGradient else DejaloColors.ScreenGradient)
        )
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        if (crisis) DejaloColors.Crisis.copy(alpha = 0.18f)
                        else DejaloColors.Teal.copy(alpha = 0.20f),
                        Color.Transparent
                    ),
                    center = Offset(w * (0.15f + shift * 0.1f), h * 0.08f),
                    radius = w * 0.7f
                ),
                radius = w * 0.7f,
                center = Offset(w * (0.15f + shift * 0.1f), h * 0.08f)
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        if (crisis) DejaloColors.Lime.copy(alpha = 0.08f)
                        else DejaloColors.Lime.copy(alpha = 0.16f),
                        Color.Transparent
                    ),
                    center = Offset(w * (0.9f - shift * 0.08f), h * 0.55f),
                    radius = w * 0.55f
                ),
                radius = w * 0.55f,
                center = Offset(w * (0.9f - shift * 0.08f), h * 0.55f)
            )
        }
        content()
    }
}
