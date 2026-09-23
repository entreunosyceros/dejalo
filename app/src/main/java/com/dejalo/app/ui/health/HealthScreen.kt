package com.dejalo.app.ui.health

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dejalo.app.data.QuitRepository
import com.dejalo.app.domain.HealthFactKind
import com.dejalo.app.domain.HealthMilestone
import com.dejalo.app.domain.HealthMilestones
import com.dejalo.app.ui.components.DejaloBackground
import com.dejalo.app.ui.components.GradientHairline
import com.dejalo.app.ui.components.SectionTitle
import com.dejalo.app.ui.components.SoftPanel
import com.dejalo.app.ui.theme.DejaloColors
import com.dejalo.app.ui.util.formatLifeRegained

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthScreen(
    repository: QuitRepository,
    onBack: () -> Unit
) {
    val stats by repository.observeLiveStats().collectAsStateWithLifecycle(initialValue = null)
    val elapsed = stats?.elapsedMillis ?: 0L
    val bodyNow = remember(elapsed) { HealthMilestones.bodyNow(elapsed) }
    val physiological = remember { HealthMilestones.all.filter { it.kind == HealthFactKind.PHYSIOLOGICAL } }
    val estimates = remember { HealthMilestones.all.filter { it.kind == HealthFactKind.ESTIMATE } }

    DejaloBackground {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text("Recuperación") },
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
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Separamos hechos fisiológicos orientativos de estimaciones poblacionales. No sustituye consejo médico.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                SoftPanel {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "¿Qué está pasando en tu cuerpo ahora?",
                            style = MaterialTheme.typography.titleMedium,
                            color = DejaloColors.Navy
                        )
                        Text(
                            text = bodyNow.headline,
                            style = MaterialTheme.typography.bodyLarge,
                            color = DejaloColors.TealDeep
                        )
                        Text(
                            text = bodyNow.detail,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (bodyNow.current != null) {
                            val animated by animateFloatAsState(
                                bodyNow.progressToCurrent,
                                label = "bodyNow"
                            )
                            LinearProgressIndicator(
                                progress = { animated },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(10.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                color = DejaloColors.Teal,
                                trackColor = DejaloColors.Line
                            )
                            Text(
                                text = "${bodyNow.completedCount}/${bodyNow.totalCount} hitos · " +
                                    "${(bodyNow.progressToCurrent * 100).toInt()}% hacia el siguiente",
                                style = MaterialTheme.typography.labelLarge,
                                color = DejaloColors.InkMuted
                            )
                        }
                    }
                }

                stats?.let { s ->
                    SoftPanel {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "Tiempo estimado de vida no expuesto al consumo",
                                style = MaterialTheme.typography.labelLarge,
                                color = DejaloColors.TealDeep
                            )
                            Text(
                                text = "≈ ${formatLifeRegained(s.lifeRegainedMinutes)}",
                                style = MaterialTheme.typography.headlineMedium,
                                color = DejaloColors.Navy
                            )
                            Text(
                                text = "Estimación basada en una referencia poblacional (~11 min por cigarrillo evitado); " +
                                    "no representa una predicción individual ni una garantía médica.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = DejaloColors.InkMuted
                            )
                        }
                    }
                }

                GradientHairline()

                SectionTitle(
                    title = "Recuperación fisiológica",
                    supporting = "Cambios descritos en materiales de cese (orientativos)"
                )
                physiological.forEach { milestone ->
                    MilestoneCard(elapsed = elapsed, milestone = milestone)
                }

                SectionTitle(
                    title = "Estimaciones poblacionales",
                    supporting = "Tendencias en grupos, no predicción personal"
                )
                estimates.forEach { milestone ->
                    MilestoneCard(elapsed = elapsed, milestone = milestone)
                }

                SoftPanel {
                    Text(
                        text = "Las fuentes son referencias públicas habituales en educación sanitaria. " +
                            "Si tienes síntomas o dudas, consulta a un profesional de la salud.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = DejaloColors.InkMuted
                    )
                }

                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun MilestoneCard(elapsed: Long, milestone: HealthMilestone) {
    val progress = HealthMilestones.progress(elapsed, milestone)
    val animated by animateFloatAsState(progress, label = milestone.id)
    SoftPanel {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = milestone.title,
                style = MaterialTheme.typography.titleMedium,
                color = if (progress >= 1f) DejaloColors.TealDeep else DejaloColors.Navy
            )
            Text(
                text = milestone.summary,
                style = MaterialTheme.typography.bodyLarge,
                color = DejaloColors.Navy
            )
            Text(
                text = milestone.explanation,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            LinearProgressIndicator(
                progress = { animated },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(8.dp)),
                color = if (progress >= 1f) DejaloColors.Teal else DejaloColors.Lime,
                trackColor = DejaloColors.Line
            )
            Text(
                text = if (progress >= 1f) "Completado" else "${(progress * 100).toInt()}%",
                style = MaterialTheme.typography.labelMedium,
                color = DejaloColors.TealDeep
            )
            Text(
                text = "Fuente: ${milestone.source}",
                style = MaterialTheme.typography.labelMedium,
                color = DejaloColors.InkMuted
            )
            if (milestone.kind == HealthFactKind.ESTIMATE) {
                Text(
                    text = "Tipo: estimación poblacional",
                    style = MaterialTheme.typography.labelMedium,
                    color = DejaloColors.Crisis.copy(alpha = 0.85f)
                )
            }
        }
    }
}
