package com.dejalo.app.ui.onboarding

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dejalo.app.data.QuitRepository
import com.dejalo.app.ui.components.BrandMark
import com.dejalo.app.ui.components.BrandOutlinedButton
import com.dejalo.app.ui.components.BrandPrimaryButton
import com.dejalo.app.ui.components.DejaloBackground
import com.dejalo.app.ui.components.GradientHairline
import com.dejalo.app.ui.components.SectionTitle
import com.dejalo.app.ui.components.SoftPanel
import com.dejalo.app.ui.theme.DejaloColors

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OnboardingScreen(
    repository: QuitRepository,
    onFinished: () -> Unit,
    onRequestNotifications: (() -> Unit)? = null,
    viewModel: OnboardingViewModel = viewModel(factory = OnboardingViewModel.factory(repository))
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var logoReady by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { logoReady = true }
    val logoScale by animateFloatAsState(
        targetValue = if (logoReady) 1f else 0.86f,
        animationSpec = tween(700),
        label = "logo"
    )

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = DejaloColors.TealDeep,
        unfocusedBorderColor = DejaloColors.Line,
        focusedLabelColor = DejaloColors.TealDeep,
        cursorColor = DejaloColors.TealDeep
    )

    DejaloBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            BrandMark(
                size = 168.dp,
                modifier = Modifier.scale(logoScale)
            )
            Text(
                text = "Tu punto de partida",
                style = MaterialTheme.typography.headlineMedium,
                color = DejaloColors.Navy,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Todo se guarda en este dispositivo. Sin cuenta. Sin servidor. Sin nube.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            GradientHairline(modifier = Modifier.padding(vertical = 6.dp))

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
                OnboardingViewModel.motivatorOptions.forEach { label ->
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

            SoftPanel {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "NOTIFICACIONES LOCALES",
                        style = MaterialTheme.typography.labelLarge,
                        color = DejaloColors.TealDeep
                    )
                    Text(
                        text = "Opcional: avisos de hitos y zonas de riesgo. Todo en el dispositivo, sin servidor.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = DejaloColors.InkMuted
                    )
                    if (onRequestNotifications != null) {
                        BrandOutlinedButton(
                            text = "Permitir avisos",
                            onClick = onRequestNotifications,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            Spacer(Modifier.height(4.dp))
            BrandPrimaryButton(
                text = if (state.saving) "Guardando…" else "Empezar",
                onClick = { viewModel.submit(onFinished) },
                enabled = !state.saving
            )
            Spacer(Modifier.height(12.dp))
        }
    }
}
