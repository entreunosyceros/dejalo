package com.dejalo.app.ui.emergency.games

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.dejalo.app.ui.components.BrandPrimaryButton
import com.dejalo.app.ui.theme.DejaloColors
import kotlinx.coroutines.delay

private data class MemoryCard(
    val id: Int,
    val symbol: String,
    val matched: Boolean = false,
    val faceUp: Boolean = false
)

private val symbols = listOf("A", "B", "C", "D", "E", "F")

@Composable
fun MemoryMatchGame(
    modifier: Modifier = Modifier,
    onEngaged: () -> Unit = {}
) {
    var cards by remember { mutableStateOf(emptyList<MemoryCard>()) }
    var firstPick by remember { mutableStateOf<Int?>(null) }
    var lock by remember { mutableStateOf(false) }
    var moves by remember { mutableIntStateOf(0) }
    var started by remember { mutableStateOf(false) }
    val allMatched = cards.isNotEmpty() && cards.all { it.matched }

    fun deal() {
        val deck = (symbols + symbols)
            .shuffled()
            .mapIndexed { index, symbol -> MemoryCard(id = index, symbol = symbol) }
        cards = deck
        firstPick = null
        lock = false
        moves = 0
        started = true
        onEngaged()
    }

    LaunchedEffect(firstPick, cards) {
        val open = cards.mapIndexedNotNull { i, c -> if (c.faceUp && !c.matched) i else null }
        if (open.size == 2) {
            lock = true
            moves++
            val (a, b) = open
            delay(550)
            cards = if (cards[a].symbol == cards[b].symbol) {
                cards.mapIndexed { i, c ->
                    if (i == a || i == b) c.copy(matched = true, faceUp = true) else c
                }
            } else {
                cards.mapIndexed { i, c ->
                    if (i == a || i == b) c.copy(faceUp = false) else c
                }
            }
            firstPick = null
            lock = false
        }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = when {
                allMatched -> "¡Hecho! $moves movimientos"
                started -> "Encuentra las parejas"
                else -> "6 parejas · mente ocupada"
            },
            style = MaterialTheme.typography.titleMedium,
            color = DejaloColors.Navy
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = if (started && !allMatched) "Movimientos: $moves" else "Un juego corto para atravesar el ansia",
            style = MaterialTheme.typography.bodyMedium,
            color = DejaloColors.InkMuted
        )
        Spacer(Modifier.height(12.dp))

        if (cards.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                cards.chunked(4).forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        row.forEach { card ->
                            val index = cards.indexOf(card)
                            val show = card.faceUp || card.matched
                            val bg by animateColorAsState(
                                targetValue = when {
                                    card.matched -> DejaloColors.Teal.copy(alpha = 0.35f)
                                    show -> DejaloColors.Cloud
                                    else -> DejaloColors.NavySoft.copy(alpha = 0.85f)
                                },
                                label = "card$index"
                            )
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(bg)
                                    .border(1.dp, DejaloColors.Line, RoundedCornerShape(12.dp))
                                    .clickable(enabled = started && !lock && !card.matched && !card.faceUp) {
                                        cards = cards.mapIndexed { i, c ->
                                            if (i == index) c.copy(faceUp = true) else c
                                        }
                                        if (firstPick == null) firstPick = index
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (show) card.symbol else "·",
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = DejaloColors.Navy,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                        // pad incomplete last row
                        repeat(4 - row.size) {
                            Spacer(Modifier.weight(1f))
                        }
                    }
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(DejaloColors.Mist.copy(alpha = 0.65f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Pulsa empezar cuando el impulso apriete",
                    style = MaterialTheme.typography.bodyLarge,
                    color = DejaloColors.InkMuted,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        Spacer(Modifier.height(12.dp))
        BrandPrimaryButton(
            text = when {
                !started -> "Empezar ahora"
                allMatched -> "Otra partida"
                else -> "Reiniciar"
            },
            onClick = ::deal
        )
    }
}
