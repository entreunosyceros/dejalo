package com.dejalo.app.ui.emergency.games

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.dejalo.app.ui.components.BrandPrimaryButton
import com.dejalo.app.ui.theme.DejaloColors
import kotlinx.coroutines.delay

@Composable
fun HoldTheWaveGame(
    modifier: Modifier = Modifier,
    onEngaged: () -> Unit = {}
) {
    var level by remember { mutableIntStateOf(1) }
    var progress by remember { mutableFloatStateOf(0f) }
    var message by remember { mutableStateOf("Mantén pulsado el círculo. La ola pasa.") }
    var completedLevels by remember { mutableIntStateOf(0) }
    var active by remember { mutableStateOf(false) }

    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val holding = active && pressed

    val targetSeconds = 6 + level * 2
    val ringScale by animateFloatAsState(
        targetValue = if (holding) 1.12f else 1f,
        label = "holdScale"
    )
    val blockScrollWhileHold = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset =
                if (holding) available else Offset.Zero
        }
    }

    LaunchedEffect(holding, active, level) {
        if (!active || !holding) {
            if (active && !holding && progress > 0f && progress < 1f) {
                message = "Soltaste antes. No pasa nada — vuelve a intentarlo."
                progress = 0f
            }
            return@LaunchedEffect
        }
        progress = 0f
        message = "Aguanta… el impulso también se cansa."
        val stepMs = 50L
        val steps = ((targetSeconds * 1000L) / stepMs).toInt().coerceAtLeast(1)
        repeat(steps) {
            if (!holding) return@LaunchedEffect
            delay(stepMs)
            progress = (it + 1) / steps.toFloat()
        }
        if (holding) {
            completedLevels++
            message = "Ola superada. Nivel $level listo."
            progress = 1f
            delay(600)
            level = (level % 5) + 1
            progress = 0f
            message = "Siguiente ola: ${6 + level * 2}s. Cuando quieras."
        }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (active) "Nivel $level · ${targetSeconds}s" else "Aguanta la ola",
            style = MaterialTheme.typography.titleMedium,
            color = DejaloColors.Navy
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = DejaloColors.InkMuted,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(16.dp))

        if (!active) {
            BrandPrimaryButton(
                text = "Empezar ahora",
                onClick = {
                    active = true
                    level = 1
                    progress = 0f
                    completedLevels = 0
                    message = "Mantén pulsado el círculo. La ola pasa."
                    onEngaged()
                }
            )
            Spacer(Modifier.height(12.dp))
        }

        Box(
            modifier = Modifier
                .size(160.dp)
                .scale(ringScale)
                .nestedScroll(blockScrollWhileHold)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(
                            if (holding) DejaloColors.Teal.copy(alpha = 0.55f)
                            else DejaloColors.Crisis.copy(alpha = 0.35f),
                            DejaloColors.Mist
                        )
                    )
                )
                .clickable(
                    enabled = active,
                    interactionSource = interaction,
                    indication = null,
                    onClick = { }
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (!active) "Listo" else if (holding) "…" else "Mantén",
                style = MaterialTheme.typography.titleLarge,
                color = DejaloColors.Navy
            )
        }

        Spacer(Modifier.height(14.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(8.dp)),
            color = DejaloColors.TealDeep,
            trackColor = DejaloColors.Line
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = if (completedLevels > 0) {
                "$completedLevels ola${if (completedLevels == 1) "" else "s"} superada${if (completedLevels == 1) "" else "s"}"
            } else {
                " "
            },
            style = MaterialTheme.typography.labelLarge,
            color = DejaloColors.TealDeep
        )

        if (active) {
            Spacer(Modifier.height(12.dp))
            BrandPrimaryButton(
                text = "Reiniciar niveles",
                onClick = {
                    level = 1
                    progress = 0f
                    completedLevels = 0
                    message = "Mantén pulsado el círculo. La ola pasa."
                    onEngaged()
                }
            )
        }
    }
}
