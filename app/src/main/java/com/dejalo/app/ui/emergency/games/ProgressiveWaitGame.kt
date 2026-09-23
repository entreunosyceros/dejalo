package com.dejalo.app.ui.emergency.games

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dejalo.app.ui.components.BrandPrimaryButton
import com.dejalo.app.ui.theme.DejaloColors
import kotlinx.coroutines.delay

@Composable
fun ProgressiveWaitGame(
    modifier: Modifier = Modifier,
    onEngaged: () -> Unit = {}
) {
    val targets = remember { listOf(10, 15, 20, 25, 30) }
    var round by remember { mutableIntStateOf(0) }
    var remaining by remember { mutableIntStateOf(0) }
    var running by remember { mutableStateOf(false) }
    var finished by remember { mutableStateOf(false) }

    LaunchedEffect(running, round) {
        if (!running) return@LaunchedEffect
        val target = targets.getOrElse(round) { 30 }
        remaining = target
        while (remaining > 0 && running) {
            delay(1000)
            remaining--
        }
        if (running && remaining <= 0) {
            if (round >= targets.lastIndex) {
                finished = true
                running = false
            } else {
                round++
            }
        }
    }

    val target = targets.getOrElse(round) { 30 }
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = when {
                finished -> "Has aguantado la serie completa."
                running -> "Ronda ${round + 1}/${targets.size}: aguanta $target s"
                else -> "Intervalos cada vez más largos. Solo espera."
            },
            style = MaterialTheme.typography.bodyLarge,
            color = DejaloColors.Navy
        )
        Spacer(Modifier.height(12.dp))
        if (running) {
            Text(
                text = "${remaining}s",
                style = MaterialTheme.typography.displayMedium,
                color = DejaloColors.Crisis
            )
            Spacer(Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { 1f - remaining.toFloat() / target },
                modifier = Modifier.fillMaxWidth().height(10.dp),
                color = DejaloColors.Teal,
                trackColor = DejaloColors.Line
            )
        }
        Spacer(Modifier.height(12.dp))
        BrandPrimaryButton(
            text = when {
                finished -> "Otra vez"
                running -> "Esperando…"
                else -> "Empezar"
            },
            onClick = {
                finished = false
                round = 0
                running = true
                onEngaged()
            },
            enabled = !running || finished
        )
    }
}
