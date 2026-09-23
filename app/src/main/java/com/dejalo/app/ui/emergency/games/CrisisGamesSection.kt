package com.dejalo.app.ui.emergency.games

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.dejalo.app.ui.components.SoftPanel
import com.dejalo.app.ui.theme.DejaloColors

@Composable
fun CrisisGamesSection(
    selected: CrisisGame?,
    crisisRunning: Boolean,
    onSelect: (CrisisGame?) -> Unit,
    onEngaged: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "MINIJUEGOS",
            style = MaterialTheme.typography.labelLarge,
            color = DejaloColors.TealDeep
        )
        Text(
            text = if (crisisRunning) {
                "Elige un juego según lo que necesites: manos, mente, esperar o relajarte."
            } else {
                "Manos, mente, espera o ritmo. Toca uno cuando te apetezca fumar."
            },
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        GameMechanism.entries.forEach { mechanism ->
            Text(
                text = mechanism.label,
                style = MaterialTheme.typography.titleMedium,
                color = DejaloColors.Navy
            )
            CrisisGame.entries.filter { it.mechanism == mechanism }.forEach { game ->
                val isSelected = selected == game
                OutlinedButton(
                    onClick = { onSelect(if (isSelected) null else game) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(
                        width = if (isSelected) 2.dp else 1.5.dp,
                        color = if (isSelected) DejaloColors.TealDeep else DejaloColors.Teal
                    ),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (isSelected) {
                            DejaloColors.Teal.copy(alpha = 0.18f)
                        } else {
                            DejaloColors.Cloud
                        },
                        contentColor = DejaloColors.Navy
                    )
                ) {
                    Text(
                        text = game.title,
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        if (selected != null) {
            SoftPanel {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = selected.blurb,
                        style = MaterialTheme.typography.bodyMedium,
                        color = DejaloColors.InkMuted
                    )
                    Spacer(Modifier.height(12.dp))
                    key(selected) {
                        when (selected) {
                            CrisisGame.BUBBLE -> BubblePopGame(onEngaged = onEngaged)
                            CrisisGame.REACTION -> ReactionTapGame(onEngaged = onEngaged)
                            CrisisGame.TRACE -> TracePathGame(onEngaged = onEngaged)
                            CrisisGame.MEMORY -> MemoryMatchGame(onEngaged = onEngaged)
                            CrisisGame.NUMBERS -> NumberOrderGame(onEngaged = onEngaged)
                            CrisisGame.HOLD -> HoldTheWaveGame(onEngaged = onEngaged)
                            CrisisGame.PROGRESSIVE -> ProgressiveWaitGame(onEngaged = onEngaged)
                            CrisisGame.RHYTHM -> RhythmTapGame(onEngaged = onEngaged)
                            CrisisGame.FOLLOW -> FollowDotGame(onEngaged = onEngaged)
                        }
                    }
                }
            }
        }
    }
}
