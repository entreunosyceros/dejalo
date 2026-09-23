package com.dejalo.app.ui.risk

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dejalo.app.data.QuitRepository
import com.dejalo.app.domain.craving.CravingRiskDetector
import com.dejalo.app.domain.craving.RiskAlert
import com.dejalo.app.domain.craving.RiskPhase
import com.dejalo.app.domain.craving.RiskZone
import com.dejalo.app.ui.components.BrandPrimaryButton
import com.dejalo.app.ui.components.DejaloBackground
import com.dejalo.app.ui.components.GradientHairline
import com.dejalo.app.ui.components.SectionTitle
import com.dejalo.app.ui.components.SoftPanel
import com.dejalo.app.ui.theme.DejaloColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RiskZonesScreen(
    repository: QuitRepository,
    onBack: () -> Unit,
    onEmergency: () -> Unit
) {
    val events by repository.observeCravings()
        .collectAsStateWithLifecycle(initialValue = emptyList())
    val zones = remember(events) { CravingRiskDetector.detectZones(events) }
    val alert = remember(events) { CravingRiskDetector.alertForNow(events) }

    DejaloBackground {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text("Zonas de riesgo") },
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
                    text = "A partir de tus episodios de ansia, Déjalo! detecta franjas horarias en las que sueles tener más ganas de fumar. Todo offline, en tu dispositivo.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                alert?.let { CurrentAlertCard(it, onEmergency) }

                GradientHairline()

                SectionTitle(
                    title = "Franjas detectadas",
                    supporting = "Últimos 7 días · hace falta repetir el patrón varios días"
                )

                if (zones.isEmpty()) {
                    SoftPanel {
                        Text(
                            text = "Aún no hay zonas claras. Cuando registres varios episodios en horas parecidas (al menos 2 días distintos), aparecerán aquí.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = DejaloColors.Navy
                        )
                    }
                } else {
                    zones.forEach { ZoneCard(it) }
                }

                SoftPanel {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "AVISOS",
                            style = MaterialTheme.typography.labelLarge,
                            color = DejaloColors.TealDeep
                        )
                        Text(
                            text = "Si activaste las notificaciones, Déjalo! puede avisarte cuando se acerque una de estas franjas, con acceso directo al modo emergencia.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun CurrentAlertCard(alert: RiskAlert, onEmergency: () -> Unit) {
    SoftPanel {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = if (alert.phase == RiskPhase.APPROACHING) {
                    "HOY · ZONA CERCA"
                } else {
                    "HOY · ZONA ACTIVA"
                },
                style = MaterialTheme.typography.labelLarge,
                color = DejaloColors.Crisis
            )
            Text(
                text = alert.title,
                style = MaterialTheme.typography.titleMedium,
                color = DejaloColors.Navy
            )
            Text(
                text = alert.body,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            BrandPrimaryButton(
                text = "Abrir modo emergencia",
                onClick = onEmergency
            )
        }
    }
}

@Composable
private fun ZoneCard(zone: RiskZone) {
    SoftPanel {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = zone.timeLabel(),
                style = MaterialTheme.typography.headlineSmall,
                color = DejaloColors.Navy
            )
            Text(
                text = "${zone.episodeCount} episodio${if (zone.episodeCount == 1) "" else "s"} · " +
                    "${zone.daysWithHits} día${if (zone.daysWithHits == 1) "" else "s"} con hits",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            zone.topTrigger?.let {
                Text(
                    text = "Desencadenante frecuente: $it",
                    style = MaterialTheme.typography.bodyMedium,
                    color = DejaloColors.InkMuted
                )
            }
        }
    }
}
