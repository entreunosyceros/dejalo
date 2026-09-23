package com.dejalo.app.ui.notnow

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dejalo.app.data.QuitRepository
import com.dejalo.app.ui.components.BrandOutlinedButton
import com.dejalo.app.ui.components.BrandPrimaryButton
import com.dejalo.app.ui.components.CrisisButton
import com.dejalo.app.ui.components.DejaloBackground
import com.dejalo.app.ui.components.HeroMetric
import com.dejalo.app.ui.components.IntensityScale
import com.dejalo.app.ui.components.SoftPanel
import com.dejalo.app.ui.theme.DejaloColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotNowScreen(
    repository: QuitRepository,
    onBack: () -> Unit,
    onEmergency: () -> Unit,
    viewModel: NotNowViewModel = viewModel(factory = NotNowViewModel.factory(repository))
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    DejaloBackground(crisis = true) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text("Ahora no") },
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
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                when (state.phase) {
                    NotNowPhase.READY -> {
                        Text(
                            text = "No estás prometiendo «nunca más». Solo decides: ahora no.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        IntensityScale(
                            title = "Intensidad ahora",
                            value = state.intensityBefore,
                            onChange = viewModel::setIntensityBefore,
                            supporting = "¿Cuánto te apetece fumar en este momento?"
                        )
                        CrisisButton(
                            text = "No voy a fumar ahora",
                            onClick = viewModel::start
                        )
                        BrandOutlinedButton(
                            text = "Prefiero el modo emergencia completo",
                            onClick = onEmergency,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    NotNowPhase.COUNTING -> {
                        Text(
                            text = "Solo cinco minutos. El pico pasa.",
                            style = MaterialTheme.typography.titleMedium,
                            color = DejaloColors.Navy,
                            textAlign = TextAlign.Center
                        )
                        HeroMetric(
                            label = "Ahora no",
                            value = "%02d:%02d".format(
                                state.remainingSec / 60,
                                state.remainingSec % 60
                            ),
                            valueStyle = MaterialTheme.typography.displayLarge,
                            valueColor = DejaloColors.Crisis
                        )
                        SoftPanel {
                            Text(
                                text = "Respira. Bebe agua. Puedes abrir un minijuego desde emergencia si lo necesitas.",
                                style = MaterialTheme.typography.bodyLarge,
                                color = DejaloColors.Navy,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        BrandOutlinedButton(
                            text = "Ya pasó — medir cómo estoy",
                            onClick = viewModel::skipToCheckIn,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    NotNowPhase.CHECK_IN -> {
                        Text(
                            text = "¿Cómo estás ahora?",
                            style = MaterialTheme.typography.headlineMedium,
                            color = DejaloColors.Navy,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Empezaste en ${state.intensityBefore}/10.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        IntensityScale(
                            title = "Intensidad ahora",
                            value = state.intensityAfter,
                            onChange = viewModel::setIntensityAfter
                        )
                        BrandPrimaryButton(
                            text = "Guardar y continuar",
                            onClick = { viewModel.saveAndFinish(onBack) }
                        )
                    }
                    NotNowPhase.DONE -> {
                        SoftPanel {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    "Lo has sostenido",
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = DejaloColors.Navy
                                )
                                Text(
                                    "${state.intensityBefore} → ${state.intensityAfter}",
                                    style = MaterialTheme.typography.displayMedium,
                                    color = DejaloColors.TealDeep
                                )
                            }
                        }
                        BrandPrimaryButton(text = "Volver", onClick = onBack)
                        BrandOutlinedButton(
                            text = "Otra vez",
                            onClick = viewModel::reset,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}
