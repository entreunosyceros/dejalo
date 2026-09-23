package com.dejalo.app.ui.emergency.games

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import com.dejalo.app.ui.components.BrandPrimaryButton
import com.dejalo.app.ui.theme.DejaloColors
import kotlinx.coroutines.delay
import kotlin.math.abs

@Composable
fun RhythmTapGame(
    modifier: Modifier = Modifier,
    onEngaged: () -> Unit = {}
) {
    var running by remember { mutableStateOf(false) }
    var score by remember { mutableIntStateOf(0) }
    var secondsLeft by remember { mutableIntStateOf(45) }
    var beat by remember { mutableStateOf(false) }
    var lastBeatAt by remember { mutableStateOf(0L) }

    val pulse by rememberInfiniteTransition(label = "rhythm").animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    LaunchedEffect(running) {
        if (!running) return@LaunchedEffect
        secondsLeft = 45
        score = 0
        while (secondsLeft > 0 && running) {
            beat = true
            lastBeatAt = System.currentTimeMillis()
            delay(500)
            beat = false
            delay(500)
            secondsLeft--
        }
        running = false
    }

    Column(modifier = modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = if (running) "Toca al compás · $score · ${secondsLeft}s" else "Toca cuando el círculo se agranda.",
            style = MaterialTheme.typography.bodyLarge,
            color = DejaloColors.Navy
        )
        Spacer(Modifier.height(16.dp))
        Box(
            modifier = Modifier
                .size(130.dp)
                .scale(if (running) pulse else 1f)
                .clip(CircleShape)
                .background(if (beat) DejaloColors.Lime.copy(alpha = 0.55f) else DejaloColors.Teal.copy(alpha = 0.35f))
                .clickable(enabled = running) {
                    onEngaged()
                    val delta = abs(System.currentTimeMillis() - lastBeatAt)
                    if (delta < 280) score++
                },
            contentAlignment = Alignment.Center
        ) {
            Text("♪", style = MaterialTheme.typography.displayMedium, color = DejaloColors.Navy)
        }
        Spacer(Modifier.height(14.dp))
        BrandPrimaryButton(
            text = if (running) "En curso…" else if (score > 0) "Otra vez ($score)" else "Empezar",
            onClick = {
                running = true
                onEngaged()
            },
            enabled = !running
        )
    }
}
