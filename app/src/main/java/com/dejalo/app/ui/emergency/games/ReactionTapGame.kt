package com.dejalo.app.ui.emergency.games

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
import androidx.compose.ui.unit.dp
import com.dejalo.app.ui.components.BrandPrimaryButton
import com.dejalo.app.ui.theme.DejaloColors
import kotlinx.coroutines.delay
import kotlin.random.Random

private enum class ReactionPhase { WAITING, GO, TOO_EARLY, RESULT }

@Composable
fun ReactionTapGame(
    modifier: Modifier = Modifier,
    onEngaged: () -> Unit = {}
) {
    var phase by remember { mutableStateOf(ReactionPhase.WAITING) }
    var hits by remember { mutableIntStateOf(0) }
    var lastMs by remember { mutableIntStateOf(0) }
    var round by remember { mutableIntStateOf(0) }

    LaunchedEffect(round) {
        if (round == 0) return@LaunchedEffect
        phase = ReactionPhase.WAITING
        delay(Random.nextLong(1200, 3200))
        if (phase == ReactionPhase.WAITING) {
            phase = ReactionPhase.GO
        }
    }

    Column(modifier = modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = if (hits == 0) "Espera al verde y toca" else "Aciertos: $hits · último: ${lastMs} ms",
            style = MaterialTheme.typography.bodyLarge,
            color = DejaloColors.Navy
        )
        Spacer(Modifier.height(16.dp))
        val color = when (phase) {
            ReactionPhase.WAITING, ReactionPhase.TOO_EARLY -> DejaloColors.Crisis
            ReactionPhase.GO -> DejaloColors.Lime
            ReactionPhase.RESULT -> DejaloColors.Teal
        }
        var goAt by remember { mutableStateOf(0L) }
        LaunchedEffect(phase) {
            if (phase == ReactionPhase.GO) goAt = System.currentTimeMillis()
        }
        Box(
            modifier = Modifier
                .size(140.dp)
                .clip(CircleShape)
                .background(color)
                .clickable {
                    onEngaged()
                    when (phase) {
                        ReactionPhase.WAITING -> phase = ReactionPhase.TOO_EARLY
                        ReactionPhase.GO -> {
                            lastMs = (System.currentTimeMillis() - goAt).toInt().coerceAtLeast(1)
                            hits++
                            phase = ReactionPhase.RESULT
                        }
                        else -> Unit
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = when (phase) {
                    ReactionPhase.WAITING -> "Espera…"
                    ReactionPhase.GO -> "¡YA!"
                    ReactionPhase.TOO_EARLY -> "Pronto"
                    ReactionPhase.RESULT -> "${lastMs} ms"
                },
                style = MaterialTheme.typography.titleLarge,
                color = DejaloColors.Navy
            )
        }
        Spacer(Modifier.height(14.dp))
        BrandPrimaryButton(
            text = when {
                round == 0 -> "Empezar"
                phase == ReactionPhase.RESULT || phase == ReactionPhase.TOO_EARLY -> "Otra ronda"
                else -> "En curso…"
            },
            onClick = {
                if (phase != ReactionPhase.WAITING && phase != ReactionPhase.GO) {
                    round++
                } else if (round == 0) {
                    round = 1
                }
            },
            enabled = phase != ReactionPhase.WAITING && phase != ReactionPhase.GO || round == 0
        )
    }
}
