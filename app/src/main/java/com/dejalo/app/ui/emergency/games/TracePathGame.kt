package com.dejalo.app.ui.emergency.games

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.dejalo.app.ui.components.BrandPrimaryButton
import com.dejalo.app.ui.theme.DejaloColors
import kotlin.math.sin

@Composable
fun TracePathGame(
    modifier: Modifier = Modifier,
    onEngaged: () -> Unit = {}
) {
    var progress by remember { mutableFloatStateOf(0f) }
    var active by remember { mutableStateOf(false) }
    var done by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = when {
                done -> "Camino completado. Bien."
                active -> "Sigue la onda con el dedo…"
                else -> "Mantén el dedo sobre la línea ondulada."
            },
            style = MaterialTheme.typography.bodyLarge,
            color = DejaloColors.Navy
        )
        Spacer(Modifier.height(12.dp))
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(16.dp))
                .pointerInput(active) {
                    if (!active) return@pointerInput
                    detectDragGestures(
                        onDragStart = { onEngaged() },
                        onDrag = { change, _ ->
                            change.consume()
                            val x = change.position.x.coerceIn(0f, size.width.toFloat())
                            val p = (x / size.width).coerceIn(0f, 1f)
                            if (p > progress) progress = p
                            if (progress >= 0.97f) {
                                done = true
                                active = false
                            }
                        }
                    )
                }
        ) {
            val w = size.width
            val h = size.height
            val path = Path()
            val steps = 40
            for (i in 0..steps) {
                val t = i / steps.toFloat()
                val x = t * w
                val y = h * 0.5f + sin(t * Math.PI.toFloat() * 3f) * h * 0.28f
                if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }
            drawPath(
                path = path,
                color = DejaloColors.Line,
                style = Stroke(width = 18f, cap = StrokeCap.Round)
            )
            val drawn = Path()
            val drawnSteps = (steps * progress).toInt().coerceAtLeast(1)
            for (i in 0..drawnSteps) {
                val t = i / steps.toFloat()
                val x = t * w
                val y = h * 0.5f + sin(t * Math.PI.toFloat() * 3f) * h * 0.28f
                if (i == 0) drawn.moveTo(x, y) else drawn.lineTo(x, y)
            }
            drawPath(
                path = drawn,
                color = DejaloColors.TealDeep,
                style = Stroke(width = 14f, cap = StrokeCap.Round)
            )
            val tipT = progress
            val tipX = tipT * w
            val tipY = h * 0.5f + sin(tipT * Math.PI.toFloat() * 3f) * h * 0.28f
            drawCircle(DejaloColors.Crisis, radius = 16f, center = Offset(tipX, tipY))
        }
        Spacer(Modifier.height(12.dp))
        BrandPrimaryButton(
            text = when {
                done -> "Otra vez"
                active -> "Trazando…"
                else -> "Empezar"
            },
            onClick = {
                if (done || !active) {
                    progress = 0f
                    done = false
                    active = true
                    onEngaged()
                }
            },
            enabled = !active || done
        )
    }
}
