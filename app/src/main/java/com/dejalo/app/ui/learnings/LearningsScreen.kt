package com.dejalo.app.ui.learnings

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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dejalo.app.data.QuitRepository
import com.dejalo.app.domain.craving.CravingInsights
import com.dejalo.app.domain.craving.CravingRelapseLink
import com.dejalo.app.domain.craving.ToolEffectivenessAnalyzer
import com.dejalo.app.domain.craving.WhatWorksForMe
import com.dejalo.app.ui.components.BrandOutlinedButton
import com.dejalo.app.ui.components.DejaloBackground
import com.dejalo.app.ui.components.GradientHairline
import com.dejalo.app.ui.components.SectionTitle
import com.dejalo.app.ui.components.SoftPanel
import com.dejalo.app.ui.theme.DejaloColors
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearningsScreen(
    repository: QuitRepository,
    onBack: () -> Unit,
    onCreateRoutine: (situation: String) -> Unit
) {
    val cravings by repository.observeCravings()
        .collectAsStateWithLifecycle(initialValue = emptyList())
    val relapses by repository.observeRelapses()
        .collectAsStateWithLifecycle(initialValue = emptyList())
    val learnings = remember(cravings, relapses) {
        CravingInsights.build(cravings, relapses)
    }

    DejaloBackground {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text("Aprendizajes") },
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
                    text = "Patrones de tu proceso: ansia, recaídas, qué te funciona y prevención. Sin juicios ni IA.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (learnings == null) {
                    SoftPanel {
                        Text(
                            text = "Todavía no hay datos suficientes. Usa el modo emergencia (mide intensidad y herramientas) y vuelve aquí.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = DejaloColors.Navy
                        )
                    }
                } else {
                    SoftPanel {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "LO QUE HAS APRENDIDO",
                                style = MaterialTheme.typography.labelLarge,
                                color = DejaloColors.TealDeep
                            )
                            Text(
                                text = learnings.headline,
                                style = MaterialTheme.typography.titleMedium,
                                color = DejaloColors.Navy
                            )
                            if (learnings.emergencyTotal > 0) {
                                Text(
                                    text = "${learnings.emergencyWins} de ${learnings.emergencyTotal} episodios registrados como superados sin fumar.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = DejaloColors.InkMuted
                                )
                            }
                        }
                    }

                    if (learnings.cravingRelapseLinks.isNotEmpty()) {
                        SectionTitle(
                            title = "Ansia → recaída",
                            supporting = "Contextos que se repiten en episodios y, a veces, en recaídas"
                        )
                        learnings.cravingRelapseLinks.forEach { link ->
                            CravingRelapsePatternCard(
                                link = link,
                                onCreateRoutine = { onCreateRoutine(link.routineSituation) }
                            )
                        }
                    }

                    learnings.whatWorks?.let { WhatWorksSection(it) }
                        ?: SoftPanel {
                            Text(
                                text = "¿Qué me funciona a mí? aparecerá cuando midas intensidad 0–10 y uses herramientas en varios episodios.",
                                style = MaterialTheme.typography.bodyLarge,
                                color = DejaloColors.Navy
                            )
                        }

                    if (learnings.items.isNotEmpty()) {
                        GradientHairline()
                        SectionTitle(
                            title = "Otros patrones",
                            supporting = "Desencadenantes, horas y recaídas"
                        )
                        learnings.items.forEach { item ->
                            SoftPanel {
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text(
                                        text = item.title,
                                        style = MaterialTheme.typography.labelLarge,
                                        color = DejaloColors.TealDeep
                                    )
                                    Text(
                                        text = item.detail,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = DejaloColors.Navy
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun CravingRelapsePatternCard(
    link: CravingRelapseLink,
    onCreateRoutine: () -> Unit
) {
    SoftPanel {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = link.label.uppercase(Locale("es", "ES")),
                style = MaterialTheme.typography.labelLarge,
                color = DejaloColors.TealDeep
            )
            Text(
                text = "Episodios de ansia frecuentes",
                style = MaterialTheme.typography.bodyMedium,
                color = DejaloColors.InkMuted
            )
            link.averageIntensity?.let { avg ->
                Text(
                    text = "Intensidad media ${String.format(Locale("es", "ES"), "%.1f", avg)}/10",
                    style = MaterialTheme.typography.titleMedium,
                    color = DejaloColors.Navy
                )
            }
            Text(
                text = "${link.cravingCount} episodio${if (link.cravingCount == 1) "" else "s"}" +
                    if (link.relapseCount > 0) {
                        "  ·  ${link.relapseCount} recaída${if (link.relapseCount == 1) "" else "s"}"
                    } else {
                        ""
                    },
                style = MaterialTheme.typography.bodyLarge,
                color = DejaloColors.Navy
            )
            Text(
                text = link.narrative,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            BrandOutlinedButton(
                text = "Crear rutina alternativa para ${link.label.lowercase(Locale("es", "ES"))}",
                onClick = onCreateRoutine,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun WhatWorksSection(whatWorks: WhatWorksForMe) {
    SectionTitle(
        title = "¿Qué me funciona a mí?",
        supporting = "Bajada media de intensidad (0–10) en episodios donde usaste cada herramienta"
    )
    SoftPanel {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text(
                text = "TUS HERRAMIENTAS MÁS EFICACES",
                style = MaterialTheme.typography.labelLarge,
                color = DejaloColors.TealDeep
            )
            val maxDrop = whatWorks.ranking.maxOf { it.averageDrop }.coerceAtLeast(0.1f)
            whatWorks.ranking.forEach { tool ->
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = tool.label,
                            style = MaterialTheme.typography.titleMedium,
                            color = DejaloColors.Navy
                        )
                        Text(
                            text = "↓ ${ToolEffectivenessAnalyzer.formatDrop(tool.averageDrop)}  ·  n=${tool.episodeCount}",
                            style = MaterialTheme.typography.labelLarge,
                            color = if (tool.averageDrop >= 0) DejaloColors.TealDeep else DejaloColors.Crisis
                        )
                    }
                    LinearProgressIndicator(
                        progress = {
                            (tool.averageDrop.coerceAtLeast(0f) / maxDrop).coerceIn(0f, 1f)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp),
                        color = DejaloColors.Teal,
                        trackColor = DejaloColors.Line
                    )
                }
            }
            Text(
                text = whatWorks.disclaimer,
                style = MaterialTheme.typography.bodyMedium,
                color = DejaloColors.InkMuted
            )
        }
    }

    whatWorks.triggerInsight?.let { insight ->
        SoftPanel {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "SEGÚN EL DESENCADENANTE",
                    style = MaterialTheme.typography.labelLarge,
                    color = DejaloColors.TealDeep
                )
                Text(
                    text = "Cuando tienes ansia por ${insight.trigger.lowercase()}, " +
                        "${insight.betterToolLabel.lowercase()} parece ayudarte más " +
                        "(↓ ${ToolEffectivenessAnalyzer.formatDrop(insight.betterDrop)}) " +
                        "que ${insight.weakerToolLabel.lowercase()} " +
                        "(↓ ${ToolEffectivenessAnalyzer.formatDrop(insight.weakerDrop)}).",
                    style = MaterialTheme.typography.bodyLarge,
                    color = DejaloColors.Navy
                )
                Text(
                    text = insight.sampleNote,
                    style = MaterialTheme.typography.bodyMedium,
                    color = DejaloColors.InkMuted
                )
            }
        }
    }
}
