package com.dejalo.app.ui.emergency.games

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.dejalo.app.ui.components.BrandPrimaryButton
import com.dejalo.app.ui.theme.DejaloColors
import kotlinx.coroutines.delay
import kotlin.math.roundToInt
import kotlin.random.Random

private data class Bubble(
    val id: Int,
    val xFrac: Float,
    val yFrac: Float,
    val sizeDp: Float,
    val colorIndex: Int
)

@Composable
fun BubblePopGame(
    modifier: Modifier = Modifier,
    onEngaged: () -> Unit = {}
) {
    var score by remember { mutableIntStateOf(0) }
    var secondsLeft by remember { mutableIntStateOf(60) }
    var running by remember { mutableStateOf(false) }
    var finished by remember { mutableStateOf(false) }
    val bubbles = remember { mutableStateListOf<Bubble>() }
    var nextId by remember { mutableIntStateOf(0) }
    val colors = remember {
        listOf(DejaloColors.Crisis, DejaloColors.Teal, DejaloColors.Lime, DejaloColors.NavySoft)
    }
    val blockParentScroll = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset =
                if (running) available else Offset.Zero
        }
    }

    fun resetRound() {
        score = 0
        secondsLeft = 60
        finished = false
        running = true
        bubbles.clear()
        onEngaged()
    }

    LaunchedEffect(running) {
        if (!running) return@LaunchedEffect
        while (secondsLeft > 0 && running) {
            delay(1000)
            secondsLeft--
        }
        if (running) {
            running = false
            finished = true
            bubbles.clear()
        }
    }

    LaunchedEffect(running) {
        if (!running) return@LaunchedEffect
        while (running) {
            if (bubbles.size < 7) {
                bubbles += Bubble(
                    id = nextId++,
                    xFrac = Random.nextFloat().coerceIn(0.08f, 0.78f),
                    yFrac = Random.nextFloat().coerceIn(0.08f, 0.72f),
                    sizeDp = Random.nextInt(48, 78).toFloat(),
                    colorIndex = Random.nextInt(colors.size)
                )
            }
            delay(650)
        }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (finished) "Ronda terminada · $score toques" else "Toca las burbujas del ansia",
            style = MaterialTheme.typography.titleMedium,
            color = DejaloColors.Navy
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = when {
                running -> "${secondsLeft}s · $score"
                finished -> "Has ocupado un minuto. ¿Otra ronda?"
                else -> "60 segundos de distracción total"
            },
            style = MaterialTheme.typography.bodyMedium,
            color = DejaloColors.InkMuted
        )
        Spacer(Modifier.height(12.dp))

        if (!running) {
            BrandPrimaryButton(
                text = if (finished) "Otra ronda" else "Empezar ahora",
                onClick = ::resetRound
            )
            Spacer(Modifier.height(12.dp))
        }

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .nestedScroll(blockParentScroll)
                .clip(RoundedCornerShape(18.dp))
                .background(DejaloColors.Mist.copy(alpha = 0.65f))
                .pointerInput(running) {
                    // Consume taps in the board so el scroll padre no las cancela.
                    detectTapGestures { }
                }
        ) {
            val widthPx = constraints.maxWidth.toFloat().coerceAtLeast(1f)
            val heightPx = constraints.maxHeight.toFloat().coerceAtLeast(1f)
            val density = LocalDensity.current

            bubbles.toList().forEach { bubble ->
                key(bubble.id) {
                    val pulse = rememberInfiniteTransition(label = "b${bubble.id}")
                    val scale by pulse.animateFloat(
                        initialValue = 0.92f,
                        targetValue = 1.08f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(900, easing = LinearEasing),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "s${bubble.id}"
                    )
                    val sizePx = with(density) { bubble.sizeDp.dp.toPx() }
                    Box(
                        modifier = Modifier
                            .offset {
                                IntOffset(
                                    (bubble.xFrac * (widthPx - sizePx)).roundToInt().coerceAtLeast(0),
                                    (bubble.yFrac * (heightPx - sizePx)).roundToInt().coerceAtLeast(0)
                                )
                            }
                            .size(bubble.sizeDp.dp)
                            .scale(scale)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(
                                        colors[bubble.colorIndex].copy(alpha = 0.95f),
                                        colors[bubble.colorIndex].copy(alpha = 0.35f)
                                    )
                                )
                            )
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) {
                                if (running) {
                                    bubbles.removeAll { it.id == bubble.id }
                                    score++
                                }
                            }
                    )
                }
            }

            if (!running) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        if (finished) "Pulsa «Otra ronda»" else "Pulsa «Empezar ahora»",
                        style = MaterialTheme.typography.bodyLarge,
                        color = DejaloColors.InkMuted
                    )
                }
            }
        }
    }
}
