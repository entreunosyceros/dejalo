package com.dejalo.app.ui.emergency.games

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NumberOrderGame(
    modifier: Modifier = Modifier,
    onEngaged: () -> Unit = {}
) {
    var nextExpected by remember { mutableIntStateOf(1) }
    var shuffled by remember { mutableStateOf((1..9).shuffled()) }
    var won by remember { mutableStateOf(false) }
    var started by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = when {
                !started -> "Toca del 1 al 9 en orden."
                won -> "¡Completado!"
                else -> "Siguiente: $nextExpected"
            },
            style = MaterialTheme.typography.bodyLarge,
            color = DejaloColors.Navy
        )
        Spacer(Modifier.height(12.dp))
        if (started) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                maxItemsInEachRow = 3
            ) {
                shuffled.forEach { n ->
                    val done = n < nextExpected
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                when {
                                    done -> DejaloColors.Teal.copy(alpha = 0.35f)
                                    n == nextExpected -> DejaloColors.CrisisSoft
                                    else -> DejaloColors.Cloud
                                }
                            )
                            .clickable(enabled = !won && !done) {
                                onEngaged()
                                if (n == nextExpected) {
                                    nextExpected++
                                    if (nextExpected > 9) won = true
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$n",
                            style = MaterialTheme.typography.headlineSmall,
                            color = DejaloColors.Navy
                        )
                    }
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        BrandPrimaryButton(
            text = when {
                !started -> "Empezar"
                won -> "Otra vez"
                else -> "En curso…"
            },
            onClick = {
                shuffled = (1..9).shuffled()
                nextExpected = 1
                won = false
                started = true
                onEngaged()
            },
            enabled = !started || won
        )
    }
}
