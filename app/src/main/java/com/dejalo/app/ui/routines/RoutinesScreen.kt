package com.dejalo.app.ui.routines

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dejalo.app.data.QuitRepository
import com.dejalo.app.data.local.entity.AlternativeRoutineEntity
import com.dejalo.app.domain.routines.AlternativeRoutineCatalog
import com.dejalo.app.ui.components.BrandOutlinedButton
import com.dejalo.app.ui.components.BrandPrimaryButton
import com.dejalo.app.ui.components.DejaloBackground
import com.dejalo.app.ui.components.GradientHairline
import com.dejalo.app.ui.components.SectionTitle
import com.dejalo.app.ui.components.SoftPanel
import com.dejalo.app.ui.theme.DejaloColors

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun RoutinesScreen(
    repository: QuitRepository,
    onBack: () -> Unit,
    prefillSituation: String? = null,
    viewModel: RoutinesViewModel = viewModel(factory = RoutinesViewModel.factory(repository))
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(prefillSituation) {
        if (!prefillSituation.isNullOrBlank()) {
            viewModel.openNewFor(prefillSituation)
        }
    }
    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = DejaloColors.TealDeep,
        unfocusedBorderColor = DejaloColors.Line,
        focusedLabelColor = DejaloColors.TealDeep,
        cursorColor = DejaloColors.TealDeep
    )

    DejaloBackground {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text("Rutinas alternativas") },
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
            if (state.loading) {
                Box(
                    Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = DejaloColors.TealDeep)
                }
                return@Scaffold
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 22.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Rompe asociaciones automáticas: en lugar de «situación → cigarrillo», define un ritual nuevo paso a paso.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (!state.editor.showEditor) {
                    BrandPrimaryButton(
                        text = "Nueva rutina",
                        onClick = viewModel::openNew
                    )
                }

                if (state.editor.showEditor) {
                    SoftPanel {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(
                                text = if (state.editor.editingId != null) "EDITAR RITUAL" else "NUEVO RITUAL",
                                style = MaterialTheme.typography.labelLarge,
                                color = DejaloColors.TealDeep
                            )
                            SectionTitle(title = "Situación")
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                AlternativeRoutineCatalog.situations.forEach { label ->
                                    FilterChip(
                                        selected = state.editor.situation == label,
                                        onClick = { viewModel.setSituation(label) },
                                        label = { Text(label) },
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
                                value = state.editor.oldPattern,
                                onValueChange = viewModel::setOldPattern,
                                label = { Text("Antes (patrón antiguo)") },
                                placeholder = { Text("Café → cigarrillo") },
                                modifier = Modifier.fillMaxWidth(),
                                colors = fieldColors
                            )
                            OutlinedTextField(
                                value = state.editor.stepsText,
                                onValueChange = viewModel::setStepsText,
                                label = { Text("Nuevo ritual (un paso por línea)") },
                                placeholder = { Text("Beber agua\n2 min de juego\n…") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(140.dp),
                                colors = fieldColors
                            )
                            BrandOutlinedButton(
                                text = "Usar sugerencia para esta situación",
                                onClick = viewModel::applySuggestion,
                                modifier = Modifier.fillMaxWidth()
                            )
                            state.editor.error?.let {
                                Text(it, color = MaterialTheme.colorScheme.error)
                            }
                            BrandPrimaryButton(
                                text = "Guardar ritual",
                                onClick = viewModel::save
                            )
                            BrandOutlinedButton(
                                text = "Cancelar",
                                onClick = viewModel::closeEditor,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                GradientHairline()

                SectionTitle(
                    title = "Tus rituales",
                    supporting = if (state.routines.isEmpty()) {
                        "Aún no hay ninguno. Crea el del café, el estrés u otra situación."
                    } else {
                        "${state.routines.size} guardado${if (state.routines.size == 1) "" else "s"}"
                    }
                )

                state.routines.forEach { routine ->
                    RoutineCard(
                        routine = routine,
                        onEdit = { viewModel.openEdit(routine) },
                        onDelete = { viewModel.delete(routine.id) }
                    )
                }

                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun RoutineCard(
    routine: AlternativeRoutineEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    SoftPanel {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = routine.situation.uppercase(),
                style = MaterialTheme.typography.labelLarge,
                color = DejaloColors.TealDeep
            )
            Text(
                text = "Antes: ${routine.oldPattern}",
                style = MaterialTheme.typography.bodyMedium,
                color = DejaloColors.InkMuted
            )
            Text(
                text = "Nuevo ritual:",
                style = MaterialTheme.typography.titleMedium,
                color = DejaloColors.Navy
            )
            routine.steps.forEachIndexed { index, step ->
                Text(
                    text = "${index + 1}. $step",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onDelete) {
                    Text("Eliminar", color = DejaloColors.Crisis)
                }
                TextButton(onClick = onEdit) {
                    Text("Editar", color = DejaloColors.TealDeep)
                }
            }
        }
    }
}
