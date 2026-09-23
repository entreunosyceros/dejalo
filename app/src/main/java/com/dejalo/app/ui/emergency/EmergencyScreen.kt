package com.dejalo.app.ui.emergency

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dejalo.app.data.QuitRepository
import com.dejalo.app.data.local.entity.AlternativeRoutineEntity
import com.dejalo.app.data.local.entity.UserProfileEntity
import com.dejalo.app.domain.routines.AlternativeRoutineCatalog
import com.dejalo.app.ui.components.BrandOutlinedButton
import com.dejalo.app.ui.components.BrandPrimaryButton
import com.dejalo.app.ui.components.CrisisButton
import com.dejalo.app.ui.components.DejaloBackground
import com.dejalo.app.ui.components.HeroMetric
import com.dejalo.app.ui.components.IntensityScale
import com.dejalo.app.ui.components.SectionTitle
import com.dejalo.app.ui.components.SoftPanel
import com.dejalo.app.ui.emergency.games.CrisisGame
import com.dejalo.app.ui.emergency.games.CrisisGamesSection
import com.dejalo.app.ui.theme.DejaloColors

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun EmergencyScreen(
    repository: QuitRepository,
    profile: UserProfileEntity?,
    onBack: () -> Unit,
    openGames: Boolean = false,
    onOpenRoutines: (() -> Unit)? = null
) {
    val motivators = profile?.let { repository.motivatorsOf(it) }.orEmpty()
    val viewModel: EmergencyViewModel = viewModel(
        factory = EmergencyViewModel.factory(repository, motivators)
    )
    val state by viewModel.state.collectAsStateWithLifecycle()
    val routineForTrigger by repository.observeRoutineFor(state.selectedTrigger)
        .collectAsStateWithLifecycle(initialValue = null)

    LaunchedEffect(openGames) {
        if (openGames && state.selectedGame == null && state.flowPhase == EmergencyPhase.ACTIVE) {
            viewModel.selectGame(CrisisGame.BUBBLE)
        }
    }

    val breathScale by animateFloatAsState(
        targetValue = when {
            !state.breathingActive -> 1f
            state.phase == BreathPhase.INHALE -> 1.28f
            state.phase == BreathPhase.HOLD -> 1.28f
            else -> 0.82f
        },
        animationSpec = tween(durationMillis = 1000),
        label = "breath"
    )
    val breathColor by animateColorAsState(
        targetValue = when {
            !state.breathingActive -> DejaloColors.Teal.copy(alpha = 0.28f)
            state.phase == BreathPhase.INHALE -> DejaloColors.Lime.copy(alpha = 0.45f)
            state.phase == BreathPhase.HOLD -> DejaloColors.Teal.copy(alpha = 0.40f)
            else -> DejaloColors.TealDeep.copy(alpha = 0.35f)
        },
        animationSpec = tween(700),
        label = "breathColor"
    )

    DejaloBackground(crisis = true) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text("Modo emergencia") },
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
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                when (state.flowPhase) {
                    EmergencyPhase.SAVED -> SavedSummary(
                        drop = state.lastDrop,
                        initial = state.intensityInitial,
                        final = state.intensityFinal,
                        onAgain = viewModel::resetForNewEpisode,
                        onBack = onBack
                    )
                    EmergencyPhase.WRAP_UP -> WrapUpSection(
                        state = state,
                        onFinalChange = viewModel::setIntensityFinal,
                        onSave = viewModel::requestWrapUpOrSave
                    )
                    EmergencyPhase.ACTIVE -> {
                        Text(
                            text = "Ganas de fumar: mide el ansia, usa herramientas y vuelve a medir.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )

                        IntensityScale(
                            title = "Intensidad ahora",
                            value = state.intensityInitial,
                            onChange = viewModel::setIntensityInitial,
                            supporting = "¿Cuánto te apetece fumar? (0 = nada, 10 = máximo)"
                        )

                        HeroMetric(
                            label = "Crisis",
                            value = "%02d:%02d".format(
                                state.crisisRemainingSec / 60,
                                state.crisisRemainingSec % 60
                            ),
                            valueStyle = MaterialTheme.typography.displayLarge,
                            valueColor = DejaloColors.Crisis
                        )

                        if (!state.crisisRunning) {
                            CrisisButton(
                                text = "Iniciar temporizador",
                                onClick = viewModel::startCrisis
                            )
                        }

                        CrisisGamesSection(
                            selected = state.selectedGame,
                            crisisRunning = state.crisisRunning,
                            onSelect = viewModel::selectGame,
                            onEngaged = viewModel::onGameEngaged
                        )

                        if (state.selectedGame == null) {
                            SoftPanel {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        "TARJETA DE DISTRACCIÓN",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = DejaloColors.TealDeep
                                    )
                                    Spacer(Modifier.height(10.dp))
                                    Text(
                                        state.distractionCard,
                                        style = MaterialTheme.typography.titleLarge,
                                        color = DejaloColors.Navy,
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(Modifier.height(14.dp))
                                    BrandOutlinedButton(
                                        text = "Otra tarjeta",
                                        onClick = viewModel::shuffleDistraction
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .size(180.dp)
                                    .scale(breathScale)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.radialGradient(
                                            colors = listOf(breathColor, breathColor.copy(alpha = 0.05f))
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        if (state.breathingActive) state.phase.label else "4-7-8",
                                        style = MaterialTheme.typography.titleLarge,
                                        color = DejaloColors.Navy
                                    )
                                    if (state.breathingActive) {
                                        Text(
                                            "${state.phaseRemaining}s",
                                            style = MaterialTheme.typography.displayMedium,
                                            color = DejaloColors.Navy
                                        )
                                    }
                                }
                            }

                            if (!state.breathingActive) {
                                BrandOutlinedButton(
                                    text = "Respiración guiada 4-7-8",
                                    onClick = viewModel::startBreathing,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            } else {
                                BrandOutlinedButton(
                                    text = "Pausar respiración",
                                    onClick = viewModel::stopBreathing,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }

                        SectionTitle(title = "Desencadenante", supporting = "¿Qué lo provocó?")
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            EmergencyViewModel.triggers.forEach { trigger ->
                                FilterChip(
                                    selected = state.selectedTrigger == trigger,
                                    onClick = { viewModel.selectTrigger(trigger) },
                                    label = { Text(trigger) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = DejaloColors.CrisisSoft,
                                        selectedLabelColor = DejaloColors.Navy,
                                        containerColor = DejaloColors.Cloud,
                                        labelColor = DejaloColors.InkMuted
                                    )
                                )
                            }
                        }

                        RitualHint(
                            situation = state.selectedTrigger,
                            saved = routineForTrigger,
                            onOpenRoutines = onOpenRoutines
                        )

                        BrandPrimaryButton(
                            text = "He terminado — medir otra vez",
                            onClick = viewModel::requestWrapUpOrSave
                        )
                    }
                }
                Spacer(Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun RitualHint(
    situation: String,
    saved: AlternativeRoutineEntity?,
    onOpenRoutines: (() -> Unit)?
) {
    val suggestion = AlternativeRoutineCatalog.suggestionFor(situation)
    SoftPanel {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "RITUAL ALTERNATIVO · ${situation.uppercase()}",
                style = MaterialTheme.typography.labelLarge,
                color = DejaloColors.TealDeep
            )
            if (saved != null) {
                Text(
                    text = "Antes: ${saved.oldPattern}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = DejaloColors.InkMuted
                )
                saved.steps.forEachIndexed { index, step ->
                    Text(
                        text = "${index + 1}. $step",
                        style = MaterialTheme.typography.bodyLarge,
                        color = DejaloColors.Navy
                    )
                }
            } else {
                Text(
                    text = "Sugerencia: ${suggestion.oldPattern} → nuevo ritual",
                    style = MaterialTheme.typography.bodyMedium,
                    color = DejaloColors.InkMuted
                )
                suggestion.suggestedSteps.forEachIndexed { index, step ->
                    Text(
                        text = "${index + 1}. $step",
                        style = MaterialTheme.typography.bodyLarge,
                        color = DejaloColors.Navy
                    )
                }
            }
            if (onOpenRoutines != null) {
                BrandOutlinedButton(
                    text = if (saved != null) "Editar mis rutinas" else "Guardar este ritual",
                    onClick = onOpenRoutines,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun WrapUpSection(
    state: EmergencyUiState,
    onFinalChange: (Int) -> Unit,
    onSave: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "¿Ha bajado el ansia?",
            style = MaterialTheme.typography.headlineMedium,
            color = DejaloColors.Navy,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Empezaste en ${state.intensityInitial}/10. ¿Cómo estás ahora?",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        IntensityScale(
            title = "Intensidad ahora",
            value = state.intensityFinal,
            onChange = onFinalChange,
            supporting = "0 = ya no me apetece · 10 = igual de fuerte"
        )
        SoftPanel {
            Column {
                Text(
                    "Resumen rápido",
                    style = MaterialTheme.typography.labelLarge,
                    color = DejaloColors.TealDeep
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "${state.intensityInitial}/10 → ${state.intensityFinal}/10" +
                        if (state.intensityInitial >= state.intensityFinal) {
                            " · bajó ${state.intensityInitial - state.intensityFinal} puntos"
                        } else {
                            " · subió ${state.intensityFinal - state.intensityInitial} puntos"
                        },
                    style = MaterialTheme.typography.titleMedium,
                    color = DejaloColors.Navy
                )
            }
        }
        BrandPrimaryButton(
            text = "Guardar episodio de ansia",
            onClick = onSave
        )
    }
}

@Composable
private fun SavedSummary(
    drop: Int?,
    initial: Int,
    final: Int,
    onAgain: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Episodio guardado",
            style = MaterialTheme.typography.headlineMedium,
            color = DejaloColors.Navy
        )
        SoftPanel {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text(
                    "$initial → $final",
                    style = MaterialTheme.typography.displayMedium,
                    color = DejaloColors.TealDeep
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = when {
                        drop == null -> "Intensidad registrada."
                        drop > 0 -> "El ansia bajó $drop punto${if (drop == 1) "" else "s"} sin fumar."
                        drop == 0 -> "Se mantuvo igual. Has aguantado el pico: cuenta."
                        else -> "Subió un poco. El próximo episodio puedes probar otra herramienta."
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    color = DejaloColors.Navy,
                    textAlign = TextAlign.Center
                )
            }
        }
        BrandPrimaryButton(text = "Otro episodio", onClick = onAgain)
        BrandOutlinedButton(text = "Volver al inicio", onClick = onBack, modifier = Modifier.fillMaxWidth())
    }
}
