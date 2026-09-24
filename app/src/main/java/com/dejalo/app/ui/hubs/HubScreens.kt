package com.dejalo.app.ui.hubs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dejalo.app.ui.components.BrandOutlinedButton
import com.dejalo.app.ui.components.CrisisButton
import com.dejalo.app.ui.components.DejaloBackground
import com.dejalo.app.ui.components.SectionTitle

@Composable
fun ProgressHubScreen(
    onHealth: () -> Unit,
    onAchievements: () -> Unit,
    onLearnings: () -> Unit,
    onAnsiaHistory: () -> Unit
) {
    HubScaffold(
        title = "Progreso",
        supporting = "Salud, logros y lo que aprendes de tu proceso."
    ) {
        BrandOutlinedButton(text = "Salud", onClick = onHealth, modifier = Modifier.fillMaxWidth())
        BrandOutlinedButton(text = "Logros", onClick = onAchievements, modifier = Modifier.fillMaxWidth())
        BrandOutlinedButton(text = "Aprendizajes de tu proceso", onClick = onLearnings, modifier = Modifier.fillMaxWidth())
        BrandOutlinedButton(text = "Historial de ansia", onClick = onAnsiaHistory, modifier = Modifier.fillMaxWidth())
    }
}

@Composable
fun ToolsHubScreen(
    onGames: () -> Unit,
    onRoutines: () -> Unit,
    onRiskZones: () -> Unit,
    onEmergency: () -> Unit
) {
    HubScaffold(
        title = "Herramientas",
        supporting = "Distracción y prevención cuando el ansia aprieta."
    ) {
        CrisisButton(text = "Modo emergencia", onClick = onEmergency)
        BrandOutlinedButton(text = "Minijuegos", onClick = onGames, modifier = Modifier.fillMaxWidth())
        BrandOutlinedButton(text = "Rutinas alternativas", onClick = onRoutines, modifier = Modifier.fillMaxWidth())
        BrandOutlinedButton(text = "Zonas de riesgo", onClick = onRiskZones, modifier = Modifier.fillMaxWidth())
    }
}

@Composable
fun SettingsHubScreen(
    onSettings: () -> Unit,
    onAbout: () -> Unit,
    onDocs: () -> Unit,
    onBackup: () -> Unit,
    onRequestNotifications: (() -> Unit)? = null
) {
    HubScaffold(
        title = "Ajustes",
        supporting = "Configuración, privacidad y copia de seguridad."
    ) {
        BrandOutlinedButton(text = "Configuración", onClick = onSettings, modifier = Modifier.fillMaxWidth())
        BrandOutlinedButton(text = "Copia de seguridad", onClick = onBackup, modifier = Modifier.fillMaxWidth())
        onRequestNotifications?.let { request ->
            BrandOutlinedButton(
                text = "Activar notificaciones",
                onClick = request,
                modifier = Modifier.fillMaxWidth()
            )
        }
        BrandOutlinedButton(text = "Documentación técnica", onClick = onDocs, modifier = Modifier.fillMaxWidth())
        BrandOutlinedButton(text = "Acerca de Déjalo!", onClick = onAbout, modifier = Modifier.fillMaxWidth())
    }
}

@Composable
private fun HubScaffold(
    title: String,
    supporting: String,
    content: @Composable () -> Unit
) {
    DejaloBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 22.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            SectionTitle(title = title, supporting = supporting)
            content()
        }
    }
}
