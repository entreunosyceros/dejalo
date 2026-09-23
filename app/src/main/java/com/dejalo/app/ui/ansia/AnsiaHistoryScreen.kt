package com.dejalo.app.ui.ansia

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dejalo.app.data.QuitRepository
import com.dejalo.app.data.local.entity.CravingEventEntity
import com.dejalo.app.domain.craving.CravingAnalyzer
import com.dejalo.app.domain.craving.CravingSummary
import com.dejalo.app.ui.components.DejaloBackground
import com.dejalo.app.ui.components.GradientHairline
import com.dejalo.app.ui.components.SectionTitle
import com.dejalo.app.ui.components.SoftPanel
import com.dejalo.app.ui.theme.DejaloColors
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnsiaHistoryScreen(
    repository: QuitRepository,
    onBack: () -> Unit
) {
    val events by repository.observeCravings()
        .collectAsStateWithLifecycle(initialValue = emptyList())
    val summary = remember(events) { CravingAnalyzer.summarize(events) }
    var expandedId by remember { mutableStateOf<Long?>(null) }

    DejaloBackground {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text("Historial de ansia") },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = DejaloColors.Navy,
                        navigationIconContentColor = DejaloColors.Navy
                    )
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 22.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Cada episodio que superas sin fumar es aprendizaje. Aquí ves el detalle y qué te dispara las ganas.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (events.isEmpty()) {
                    SoftPanel {
                        Text(
                            text = "Aún no hay episodios. Cuando uses el modo emergencia y guardes, aparecerán aquí.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = DejaloColors.Navy
                        )
                    }
                } else {
                    summary?.let { StatsHeader(it) }

                    GradientHairline()

                    summary?.let { TriggersSection(it) }

                    GradientHairline()

                    SectionTitle(
                        title = "Episodios",
                        supporting = "${events.size} registrado${if (events.size == 1) "" else "s"}"
                    )

                    events.forEach { event ->
                        val number = CravingAnalyzer.episodeNumber(events, event)
                        val expanded = expandedId == event.id
                        EpisodeCard(
                            number = number,
                            event = event,
                            expanded = expanded,
                            onClick = {
                                expandedId = if (expanded) null else event.id
                            }
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun StatsHeader(summary: CravingSummary) {
    SoftPanel {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "RESUMEN",
                style = MaterialTheme.typography.labelLarge,
                color = DejaloColors.TealDeep
            )
            Text(
                text = "${summary.totalEpisodes} episodio${if (summary.totalEpisodes == 1) "" else "s"} superado${if (summary.totalEpisodes == 1) "" else "s"}",
                style = MaterialTheme.typography.titleMedium,
                color = DejaloColors.Navy
            )
            val drop = summary.averageDrop
            if (drop != null && summary.episodesWithIntensity > 0) {
                Text(
                    text = if (drop >= 0) {
                        "Tus ganas suelen bajar una media de ${fmt1(drop)} puntos después de usar las herramientas."
                    } else {
                        "De media la intensidad aún no baja: prueba otra herramienta en el próximo pico."
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            summary.peakHourBand?.let { band ->
                val endLabel = "%02d:00".format(band.endHour)
                Text(
                    text = "La mayoría de tus episodios aparecen entre las ${"%02d:00".format(band.startHour)} y las $endLabel (${band.count} de ${summary.totalEpisodes}).",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun TriggersSection(summary: CravingSummary) {
    SectionTitle(
        title = "Desencadenantes",
        supporting = "Qué situaciones te ponen más en riesgo"
    )

    if (summary.topTriggerThisWeek != null && summary.topTriggerThisWeekCount > 0) {
        SoftPanel {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "ESTA SEMANA",
                    style = MaterialTheme.typography.labelLarge,
                    color = DejaloColors.TealDeep
                )
                Text(
                    text = "Tu desencadenante más frecuente esta semana es ${summary.topTriggerThisWeek.lowercase()} (${summary.topTriggerThisWeekCount}).",
                    style = MaterialTheme.typography.bodyLarge,
                    color = DejaloColors.Navy
                )
            }
        }
    }

    if (summary.triggersAllTime.isNotEmpty()) {
        SoftPanel {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "TODOS LOS TIEMPOS",
                    style = MaterialTheme.typography.labelLarge,
                    color = DejaloColors.TealDeep
                )
                val max = summary.triggersAllTime.maxOf { it.count }.coerceAtLeast(1)
                summary.triggersAllTime.forEach { item ->
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = item.trigger,
                                style = MaterialTheme.typography.titleMedium,
                                color = DejaloColors.Navy
                            )
                            Text(
                                text = "${item.count}",
                                style = MaterialTheme.typography.labelLarge,
                                color = DejaloColors.InkMuted
                            )
                        }
                        LinearProgressIndicator(
                            progress = { item.count.toFloat() / max },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp),
                            color = DejaloColors.Teal,
                            trackColor = DejaloColors.Line
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EpisodeCard(
    number: Int,
    event: CravingEventEntity,
    expanded: Boolean,
    onClick: () -> Unit
) {
    val dateFmt = remember {
        SimpleDateFormat("dd MMM yyyy · HH:mm", Locale("es", "ES"))
    }
    SoftPanel(
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Episodio #$number",
                    style = MaterialTheme.typography.titleMedium,
                    color = DejaloColors.Navy
                )
                Text(
                    text = if (expanded) "Ocultar" else "Ver detalle",
                    style = MaterialTheme.typography.labelLarge,
                    color = DejaloColors.TealDeep
                )
            }
            Text(
                text = dateFmt.format(Date(event.triggeredAtMillis)),
                style = MaterialTheme.typography.bodyMedium,
                color = DejaloColors.InkMuted
            )
            val drop = event.intensityDrop
            Text(
                text = buildString {
                    append(event.trigger)
                    if (event.intensityInitial in 0..10 && event.intensityFinal in 0..10) {
                        append(" · ${event.intensityInitial} → ${event.intensityFinal}")
                        if (drop != null && drop > 0) append(" (−$drop)")
                    }
                    if (event.durationSeconds > 0) {
                        append(" · ${CravingAnalyzer.formatDuration(event.durationSeconds)}")
                    }
                },
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (expanded) {
                Spacer(Modifier.height(6.dp))
                GradientHairline()
                Spacer(Modifier.height(6.dp))
                EpisodeDetail(event)
            }
        }
    }
}

@Composable
private fun EpisodeDetail(event: CravingEventEntity) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "✓ Superado sin fumar",
            style = MaterialTheme.typography.bodyLarge,
            color = DejaloColors.TealDeep
        )
        if (event.intensityInitial in 0..10) {
            Text(
                text = "Intensidad inicial: ${event.intensityInitial}/10",
                style = MaterialTheme.typography.bodyLarge,
                color = DejaloColors.Navy
            )
        }
        if (event.durationSeconds > 0) {
            Text(
                text = "Duración: ${CravingAnalyzer.formatDuration(event.durationSeconds)}",
                style = MaterialTheme.typography.bodyLarge,
                color = DejaloColors.Navy
            )
        }
        Text(
            text = "Desencadenante: ${event.trigger.lowercase()}",
            style = MaterialTheme.typography.bodyLarge,
            color = DejaloColors.Navy
        )
        val tools = CravingAnalyzer.toolLabels(event.toolsCsv)
        if (tools.isNotEmpty()) {
            Text(
                text = "Herramientas utilizadas:",
                style = MaterialTheme.typography.bodyLarge,
                color = DejaloColors.Navy
            )
            tools.forEach { tool ->
                Text(
                    text = "  ✓ $tool",
                    style = MaterialTheme.typography.bodyLarge,
                    color = DejaloColors.TealDeep
                )
            }
        } else {
            Text(
                text = "Herramientas: no registradas",
                style = MaterialTheme.typography.bodyMedium,
                color = DejaloColors.InkMuted
            )
        }
        if (event.intensityFinal in 0..10) {
            Text(
                text = "Intensidad final: ${event.intensityFinal}/10",
                style = MaterialTheme.typography.bodyLarge,
                color = DejaloColors.Navy
            )
        }
    }
}

private fun fmt1(value: Float): String =
    String.format(Locale("es", "ES"), "%.1f", value)
