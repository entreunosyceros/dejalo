package com.dejalo.app.ui.relapse

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dejalo.app.data.QuitRepository
import com.dejalo.app.domain.LiveQuitStats
import com.dejalo.app.domain.relapse.RelapseAnalyzer
import com.dejalo.app.ui.components.BrandOutlinedButton
import com.dejalo.app.ui.components.BrandPrimaryButton
import com.dejalo.app.ui.components.DejaloBackground
import com.dejalo.app.ui.components.GradientHairline
import com.dejalo.app.ui.components.SectionTitle
import com.dejalo.app.ui.components.SoftPanel
import com.dejalo.app.ui.theme.DejaloColors
import com.dejalo.app.ui.util.formatCigarettes
import com.dejalo.app.ui.util.formatEuros
import com.dejalo.app.ui.util.formatLifeRegained
import com.dejalo.app.ui.util.formatWholeDays
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun RelapseScreen(
    repository: QuitRepository,
    onBack: () -> Unit
) {
    var cigarettes by remember { mutableStateOf("1") }
    var cause by remember { mutableStateOf(RelapseAnalyzer.causeOptions.first()) }
    var notes by remember { mutableStateOf("") }
    var nextPlan by remember { mutableStateOf("") }
    var savedSnapshot by remember { mutableStateOf<LiveQuitStats?>(null) }
    var savedCigs by remember { mutableStateOf(1) }
    val scope = rememberCoroutineScope()
    val history by repository.observeRelapses().collectAsStateWithLifecycle(initialValue = emptyList())
    val stats by repository.observeLiveStats().collectAsStateWithLifecycle(initialValue = null)
    val dateFmt = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("es", "ES")) }
    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = DejaloColors.TealDeep,
        unfocusedBorderColor = DejaloColors.Line,
        focusedLabelColor = DejaloColors.TealDeep,
        cursorColor = DejaloColors.TealDeep
    )
    val relapseSummary = remember(history) { RelapseAnalyzer.summarize(history) }

    DejaloBackground {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text(if (savedSnapshot != null) "Tu proceso continúa" else "Registrar recaída") },
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
                if (savedSnapshot != null) {
                    RelapseContinueSummary(
                        cigarettes = savedCigs,
                        preserved = savedSnapshot!!,
                        onDone = onBack,
                        onAnother = {
                            savedSnapshot = null
                            cigarettes = "1"
                            notes = ""
                            nextPlan = ""
                        }
                    )
                } else {
                    Text(
                        text = "Una caída no borra tu esfuerzo. Ajustamos la racha y conservamos el historial.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    OutlinedTextField(
                        value = cigarettes,
                        onValueChange = { cigarettes = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Cigarrillos") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = fieldColors
                    )

                    SectionTitle(title = "¿Qué ocurrió?", supporting = "¿Qué pasó?")
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        RelapseAnalyzer.causeOptions.forEach { option ->
                            FilterChip(
                                selected = cause == option,
                                onClick = { cause = option },
                                label = { Text(option) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = DejaloColors.Teal.copy(alpha = 0.25f),
                                    selectedLabelColor = DejaloColors.Navy,
                                    containerColor = DejaloColors.Cloud,
                                    labelColor = DejaloColors.InkMuted
                                )
                            )
                        }
                    }

                    OutlinedTextField(
                        value = nextPlan,
                        onValueChange = { nextPlan = it },
                        label = { Text("¿Qué podrías probar la próxima vez?") },
                        supportingText = {
                            Text("Convierte la recaída en información para el siguiente intento.")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = fieldColors
                    )

                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Notas (opcional)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = fieldColors
                    )

                    BrandPrimaryButton(
                        text = "Guardar sin castigo",
                        onClick = {
                            val cigs = cigarettes.toIntOrNull() ?: 1
                            val before = stats
                            scope.launch {
                                repository.logRelapse(
                                    cigarettes = cigs,
                                    cause = cause,
                                    notes = notes,
                                    nextPlan = nextPlan
                                )
                                savedCigs = cigs
                                savedSnapshot = before
                            }
                        }
                    )
                }

                if (history.isNotEmpty()) {
                    GradientHairline()
                    SectionTitle(title = "Historial de recaídas")
                    relapseSummary?.let { summary ->
                        SoftPanel {
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = "${summary.totalRelapses} registro${if (summary.totalRelapses == 1) "" else "s"} · ${summary.totalCigarettes} cigarrillo${if (summary.totalCigarettes == 1) "" else "s"}",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = DejaloColors.Navy
                                )
                                summary.topCause?.let {
                                    Text(
                                        text = "Causa más frecuente: $it",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                    history.take(12).forEach { event ->
                        SoftPanel {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = "${dateFmt.format(Date(event.occurredAtMillis))} · ${event.cigarettes} cig. · ${event.cause}",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = DejaloColors.Navy
                                )
                                if (event.nextPlan.isNotBlank()) {
                                    Text(
                                        text = "Próxima vez: ${event.nextPlan}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = DejaloColors.TealDeep
                                    )
                                }
                            }
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun RelapseContinueSummary(
    cigarettes: Int,
    preserved: LiveQuitStats,
    onDone: () -> Unit,
    onAnother: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        SoftPanel {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Has fumado $cigarettes cigarrillo${if (cigarettes == 1) "" else "s"}. Tu proceso continúa.",
                    style = MaterialTheme.typography.titleMedium,
                    color = DejaloColors.Navy
                )
                Text(
                    text = "No has vuelto a cero.",
                    style = MaterialTheme.typography.headlineSmall,
                    color = DejaloColors.Navy
                )
                Text(
                    text = "La racha actual se reinicia. El progreso acumulado, los cigarrillos evitados y el ahorro siguen contando.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        SoftPanel {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "SIGUE CONTANDO",
                    style = MaterialTheme.typography.labelLarge,
                    color = DejaloColors.TealDeep
                )
                Text(
                    text = "✓ Mejor racha: ${formatWholeDays(preserved.bestStreakDays)}",
                    color = DejaloColors.Navy
                )
                Text(
                    text = "✓ Progreso acumulado: ${formatWholeDays(preserved.accumulatedCleanDays)} (con interrupciones)",
                    color = DejaloColors.Navy
                )
                Text(
                    text = "✓ ${formatCigarettes(preserved.cigarettesAvoided)} cigarrillos evitados",
                    color = DejaloColors.Navy
                )
                Text(
                    text = "✓ ${formatEuros(preserved.moneySavedEuros)} no gastados",
                    color = DejaloColors.Navy
                )
                Text(
                    text = "✓ ${formatLifeRegained(preserved.lifeRegainedMinutes)} de vida recuperada (estimación)",
                    color = DejaloColors.Navy
                )
            }
        }

        BrandPrimaryButton(text = "Volver al inicio", onClick = onDone)
        BrandOutlinedButton(
            text = "Registrar otra",
            onClick = onAnother,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
