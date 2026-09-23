package com.dejalo.app.ui.dashboard

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dejalo.app.data.QuitRepository
import com.dejalo.app.domain.LiveQuitStats
import com.dejalo.app.domain.craving.CravingSummary
import com.dejalo.app.domain.craving.RiskAlert
import com.dejalo.app.domain.craving.RiskPhase
import com.dejalo.app.ui.components.BrandOutlinedButton
import com.dejalo.app.ui.components.BrandPrimaryButton
import com.dejalo.app.ui.components.BrandWordmark
import com.dejalo.app.ui.components.CrisisButton
import com.dejalo.app.ui.components.DejaloBackground
import com.dejalo.app.ui.components.GradientHairline
import com.dejalo.app.ui.components.HeroMetric
import com.dejalo.app.ui.components.SectionTitle
import com.dejalo.app.ui.components.SoftPanel
import com.dejalo.app.ui.components.StatPair
import com.dejalo.app.ui.theme.DejaloColors
import com.dejalo.app.ui.util.formatCigarettes
import com.dejalo.app.ui.util.formatDurationParts
import com.dejalo.app.ui.util.formatEuros
import com.dejalo.app.ui.util.formatLifeRegained
import com.dejalo.app.ui.util.formatWholeDays
import java.util.Locale

@Composable
fun DashboardScreen(
    repository: QuitRepository,
    onEmergency: () -> Unit,
    onNotNow: () -> Unit,
    onGames: () -> Unit,
    onHealth: () -> Unit,
    onAchievements: () -> Unit,
    onRelapse: () -> Unit,
    onDocs: () -> Unit,
    onSettings: () -> Unit,
    onAbout: () -> Unit,
    onAnsiaHistory: () -> Unit,
    onRoutines: () -> Unit,
    onRiskZones: () -> Unit,
    onLearnings: () -> Unit,
    viewModel: DashboardViewModel = viewModel(factory = DashboardViewModel.factory(repository))
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val stats = state.stats
    val profile = state.profile

    if (state.loading || stats == null || profile == null) {
        DejaloBackground {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = DejaloColors.TealDeep)
            }
        }
        return
    }

    val goalProgress by animateFloatAsState(
        targetValue = stats.savingsGoalProgress,
        label = "goal"
    )

    DejaloBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 22.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(22.dp)
        ) {
            BrandWordmark(subtitle = "¡Tú puedes!")

            state.riskAlert?.let { alert ->
                RiskAlertBanner(
                    alert = alert,
                    onEmergency = onEmergency,
                    onDetails = onRiskZones
                )
            }

            HeroMetric(
                label = "Racha actual",
                value = formatDurationParts(
                    stats.streakDays,
                    stats.streakHours,
                    stats.streakMinutes,
                    stats.streakSeconds
                )
            )

            ProgressIdentityPanel(stats = stats)

            GradientHairline()

            StatPair(
                leftLabel = "Ahorro",
                leftValue = formatEuros(stats.moneySavedEuros),
                rightLabel = "Vida recuperada",
                rightValue = "≈ ${formatLifeRegained(stats.lifeRegainedMinutes)}"
            )

            Text(
                text = "Estimación poblacional (~11 min/cigarrillo); no es una predicción individual.",
                style = MaterialTheme.typography.bodyMedium,
                color = DejaloColors.InkMuted
            )

            if (profile.savingsGoalEuros > 0) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    SectionTitle(
                        title = "Meta",
                        supporting = profile.savingsGoalLabel.ifBlank { "Tu objetivo de ahorro" }
                    )
                    LinearProgressIndicator(
                        progress = { goalProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        color = DejaloColors.Teal,
                        trackColor = DejaloColors.Line
                    )
                    Text(
                        text = "${formatEuros(stats.moneySavedEuros)} / ${formatEuros(profile.savingsGoalEuros)}",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            CrisisButton(text = "Modo emergencia", onClick = onEmergency)

            CrisisButton(
                text = "No voy a fumar ahora",
                onClick = onNotNow
            )

            CrisisButton(
                text = "Minijuegos — distraer el ansia",
                onClick = onGames
            )

            state.cravingSummary?.let { summary ->
                CravingInsightPanel(summary = summary, onOpenHistory = onAnsiaHistory)
            }

            BrandOutlinedButton(
                text = "Historial de ansia",
                onClick = onAnsiaHistory,
                modifier = Modifier.fillMaxWidth()
            )

            BrandOutlinedButton(
                text = "Rutinas alternativas",
                onClick = onRoutines,
                modifier = Modifier.fillMaxWidth()
            )

            BrandOutlinedButton(
                text = "Zonas de riesgo",
                onClick = onRiskZones,
                modifier = Modifier.fillMaxWidth()
            )

            BrandOutlinedButton(
                text = "Aprendizajes de tu proceso",
                onClick = onLearnings,
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                BrandOutlinedButton(
                    text = "Salud",
                    onClick = onHealth,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Favorite, contentDescription = null, tint = DejaloColors.TealDeep)
                    Spacer(Modifier.width(6.dp))
                    Text("Salud", style = MaterialTheme.typography.titleMedium)
                }
                BrandOutlinedButton(
                    text = "Logros",
                    onClick = onAchievements,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = DejaloColors.TealDeep)
                    Spacer(Modifier.width(6.dp))
                    Text("Logros", style = MaterialTheme.typography.titleMedium)
                }
            }

            BrandOutlinedButton(
                text = "Configuración",
                onClick = onSettings,
                modifier = Modifier.fillMaxWidth()
            )

            BrandOutlinedButton(
                text = "Registrar recaída",
                onClick = onRelapse,
                modifier = Modifier.fillMaxWidth()
            )

            BrandOutlinedButton(
                text = "Documentación técnica",
                onClick = onDocs,
                modifier = Modifier.fillMaxWidth()
            )

            BrandOutlinedButton(
                text = "Acerca de Déjalo!",
                onClick = onAbout,
                modifier = Modifier.fillMaxWidth()
            )

            if (state.badges.isNotEmpty()) {
                Text(
                    text = "${state.badges.size} medalla${if (state.badges.size == 1) "" else "s"} · ¡sigue!",
                    style = MaterialTheme.typography.labelLarge,
                    color = DejaloColors.TealDeep,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun ProgressIdentityPanel(stats: LiveQuitStats) {
    SoftPanel {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            ProgressMetricRow(
                title = "Mejor racha",
                value = formatWholeDays(stats.bestStreakDays)
            )
            ProgressMetricRow(
                title = "Progreso acumulado",
                value = formatWholeDays(stats.accumulatedCleanDays),
                supporting = if (stats.hasInterruptions) {
                    "días sin fumar, con interrupciones"
                } else {
                    "días sin fumar"
                }
            )
            ProgressMetricRow(
                title = "Cigarrillos evitados",
                value = formatCigarettes(stats.cigarettesAvoided)
            )
        }
    }
}

@Composable
private fun ProgressMetricRow(
    title: String,
    value: String,
    supporting: String? = null
) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
            text = title.uppercase(),
            style = MaterialTheme.typography.labelLarge,
            color = DejaloColors.TealDeep
        )
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            color = DejaloColors.Navy
        )
        if (supporting != null) {
            Text(
                text = supporting,
                style = MaterialTheme.typography.bodyMedium,
                color = DejaloColors.InkMuted
            )
        }
    }
}

@Composable
private fun RiskAlertBanner(
    alert: RiskAlert,
    onEmergency: () -> Unit,
    onDetails: () -> Unit
) {
    SoftPanel {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = if (alert.phase == RiskPhase.APPROACHING) {
                    "HOY TIENES UNA ZONA DE RIESGO"
                } else {
                    "ZONA DE RIESGO ACTIVA"
                },
                style = MaterialTheme.typography.labelLarge,
                color = DejaloColors.Crisis
            )
            Text(
                text = "Franja ${alert.zone.timeLabel()}. " +
                    (alert.zone.topTrigger?.let { "Suele aparecer con $it. " } ?: "") +
                    "¿Preparado?",
                style = MaterialTheme.typography.bodyLarge,
                color = DejaloColors.Navy
            )
            BrandPrimaryButton(
                text = "Abrir modo emergencia",
                onClick = onEmergency
            )
            BrandOutlinedButton(
                text = "Ver zonas de riesgo",
                onClick = onDetails,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun CravingInsightPanel(
    summary: CravingSummary,
    onOpenHistory: () -> Unit
) {
    SoftPanel(
        modifier = Modifier.clickable(onClick = onOpenHistory)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "ANSIA SUPERADA",
                style = MaterialTheme.typography.labelLarge,
                color = DejaloColors.TealDeep
            )
            Text(
                text = when (summary.totalEpisodes) {
                    1 -> "1 episodio registrado"
                    else -> "${summary.totalEpisodes} episodios registrados"
                },
                style = MaterialTheme.typography.titleMedium,
                color = DejaloColors.Navy
            )
            val drop = summary.averageDrop
            if (drop != null && summary.episodesWithIntensity > 0) {
                Text(
                    text = if (drop >= 0) {
                        "De media el ansia baja ${String.format(Locale("es", "ES"), "%.1f", drop)} puntos (0–10)."
                    } else {
                        "De media el ansia aún sube un poco: prueba otra herramienta en el próximo pico."
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                val initial = summary.averageInitial
                val final = summary.averageFinal
                if (initial != null && final != null) {
                    Text(
                        text = String.format(
                            Locale("es", "ES"),
                            "Media: %.1f → %.1f",
                            initial,
                            final
                        ),
                        style = MaterialTheme.typography.labelLarge,
                        color = DejaloColors.InkMuted
                    )
                }
            } else {
                Text(
                    text = "En el próximo modo emergencia mide el ansia antes y después (0–10).",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            summary.topTriggerThisWeek?.let { trigger ->
                Text(
                    text = "Esta semana destaca: $trigger (${summary.topTriggerThisWeekCount})",
                    style = MaterialTheme.typography.bodyMedium,
                    color = DejaloColors.InkMuted
                )
            } ?: summary.topTrigger?.let { trigger ->
                Text(
                    text = "Desencadenante más frecuente: $trigger (${summary.topTriggerCount})",
                    style = MaterialTheme.typography.bodyMedium,
                    color = DejaloColors.InkMuted
                )
            }
            summary.peakHourBand?.let { band ->
                Text(
                    text = "Franja habitual: ${"%02d:00".format(band.startHour)}–${"%02d:00".format(band.endHour)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = DejaloColors.InkMuted
                )
            }
            Text(
                text = "Toca para ver historial y desencadenantes →",
                style = MaterialTheme.typography.labelLarge,
                color = DejaloColors.TealDeep
            )
        }
    }
}
