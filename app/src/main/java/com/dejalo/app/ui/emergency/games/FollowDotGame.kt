package com.dejalo.app.ui.emergency.games

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.dejalo.app.ui.components.BrandPrimaryButton
import com.dejalo.app.ui.theme.DejaloColors
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
fun FollowDotGame(
    modifier: Modifier = Modifier,
    onEngaged: () -> Unit = {}
) {
    var running by remember { mutableStateOf(false) }
    var score by remember { mutableIntStateOf(0) }
    var secondsLeft by remember { mutableIntStateOf(40) }
    var fingerX by remember { mutableFloatStateOf(0.5f) }
    var fingerY by remember { mutableFloatStateOf(0.5f) }

    val t by rememberInfiniteTransition(label = "follow").animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "t"
    )

    LaunchedEffect(running) {
        if (!running) return@LaunchedEffect
        score = 0
        secondsLeft = 40
        while (secondsLeft > 0 && running) {
            delay(1000)
            secondsLeft--
        }
        running = false
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = if (running) "Sigue el punto · $score · ${secondsLeft}s" else "Mantén el dedo cerca del punto móvil.",
            style = MaterialTheme.typography.bodyLarge,
            color = DejaloColors.Navy
        )
        Spacer(Modifier.height(12.dp))
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(DejaloColors.Mist)
                .pointerInput(running) {
                    if (!running) return@pointerInput
                    detectDragGestures(
                        onDragStart = {
                            onEngaged()
                            fingerX = (it.x / size.width).coerceIn(0f, 1f)
                            fingerY = (it.y / size.height).coerceIn(0f, 1f)
                        },
                        onDrag = { change, _ ->
                            change.consume()
                            fingerX = (change.position.x / size.width).coerceIn(0f, 1f)
                            fingerY = (change.position.y / size.height).coerceIn(0f, 1f)
                            val angle = t * Math.PI.toFloat() * 2f
                            val tx = 0.5f + 0.35f * cos(angle)
                            val ty = 0.5f + 0.28f * sin(angle * 1.3f)
                            val dist = hypot(fingerX - tx, fingerY - ty)
                            if (dist < 0.12f) score++
                        }
                    )
                }
        ) {
            val density = LocalDensity.current
            val wPx = with(density) { maxWidth.toPx() }
            val hPx = with(density) { maxHeight.toPx() }
            val angle = t * Math.PI.toFloat() * 2f
            val tx = 0.5f + 0.35f * cos(angle)
            val ty = 0.5f + 0.28f * sin(angle * 1.3f)
            Box(
                modifier = Modifier
                    .offset {
                        IntOffset(
                            (tx * wPx - 22).roundToInt(),
                            (ty * hPx - 22).roundToInt()
                        )
                    }
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(DejaloColors.Crisis)
            )
            if (running) {
                Box(
                    modifier = Modifier
                        .offset {
                            IntOffset(
                                (fingerX * wPx - 14).roundToInt(),
                                (fingerY * hPx - 14).roundToInt()
                            )
                        }
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(DejaloColors.TealDeep.copy(alpha = 0.55f))
                )
            }
        }
        Spacer(Modifier.height(12.dp))
        BrandPrimaryButton(
            text = if (running) "Siguiendo…" else if (score > 0) "Otra vez ($score)" else "Empezar",
            onClick = {
                running = true
                onEngaged()
            },
            enabled = !running
        )
    }
}
