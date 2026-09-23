package com.dejalo.app.ui.settings

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
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dejalo.app.data.QuitRepository
import com.dejalo.app.ui.components.BrandPrimaryButton
import com.dejalo.app.ui.components.DejaloBackground
import com.dejalo.app.ui.components.SectionTitle
import com.dejalo.app.ui.onboarding.QuitDateTimePicker
import com.dejalo.app.ui.theme.DejaloColors

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SettingsScreen(
    repository: QuitRepository,
    onBack: () -> Unit,
    viewModel: SettingsViewModel = viewModel(factory = SettingsViewModel.factory(repository))
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

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
                    title = { Text("Configuración") },
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
                    Modifier
                        .fillMaxSize()
                        .padding(padding),
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
                    text = "Actualiza consumo, precio, motivadores o la meta. Las métricas se recalculan con los nuevos valores.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                QuitDateTimePicker(
                    value = state.quitDateTime,
                    onValueChange = { dt -> viewModel.update { it.copy(quitDateTime = dt) } },
                    modifier = Modifier.fillMaxWidth()
                )

                SectionTitle(title = "Consumo")
                OutlinedTextField(
                    value = state.cigarettesPerDay,
                    onValueChange = { viewModel.update { s -> s.copy(cigarettesPerDay = it) } },
                    label = { Text("Cigarrillos al día") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = fieldColors
                )
                OutlinedTextField(
                    value = state.packPrice,
                    onValueChange = { viewModel.update { s -> s.copy(packPrice = it) } },
                    label = { Text("Precio del paquete (€)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = fieldColors
                )
                OutlinedTextField(
                    value = state.cigarettesPerPack,
                    onValueChange = { viewModel.update { s -> s.copy(cigarettesPerPack = it) } },
                    label = { Text("Cigarrillos por paquete") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = fieldColors
                )

                SectionTitle(
                    title = "Motivadores",
                    supporting = "¿Por qué quieres dejarlo?"
                )
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SettingsViewModel.motivatorOptions.forEach { label ->
                        val selected = label in state.selectedMotivators
                        FilterChip(
                            selected = selected,
                            onClick = { viewModel.toggleMotivator(label) },
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
                    value = state.customMotivator,
                    onValueChange = { viewModel.update { s -> s.copy(customMotivator = it) } },
                    label = { Text("Motivador personal (opcional)") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = fieldColors
                )

                SectionTitle(
                    title = "Meta de ahorro",
                    supporting = "Opcional — un objetivo concreto ayuda."
                )
                OutlinedTextField(
                    value = state.savingsGoalLabel,
                    onValueChange = { viewModel.update { s -> s.copy(savingsGoalLabel = it) } },
                    label = { Text("¿Qué quieres conseguir?") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = fieldColors
                )
                OutlinedTextField(
                    value = state.savingsGoalEuros,
                    onValueChange = { viewModel.update { s -> s.copy(savingsGoalEuros = it) } },
                    label = { Text("Precio objetivo (€)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = fieldColors
                )

                state.error?.let {
                    Text(it, color = MaterialTheme.colorScheme.error)
                }

                BrandPrimaryButton(
                    text = when {
                        state.saving -> "Guardando…"
                        state.saved -> "Guardado"
                        else -> "Guardar cambios"
                    },
                    onClick = { viewModel.save(onDone = onBack) },
                    enabled = !state.saving
                )
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}
