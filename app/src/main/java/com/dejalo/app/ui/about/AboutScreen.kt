package com.dejalo.app.ui.about

import android.content.Intent
import android.net.Uri
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.dejalo.app.ui.components.BrandMark
import com.dejalo.app.ui.components.BrandOutlinedButton
import com.dejalo.app.ui.components.DEJALO_GITHUB_URL
import com.dejalo.app.ui.components.DejaloBackground
import com.dejalo.app.ui.components.GradientHairline
import com.dejalo.app.ui.components.SoftPanel
import com.dejalo.app.ui.theme.DejaloColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    onBack: () -> Unit,
    onDocs: () -> Unit
) {
    val context = LocalContext.current

    DejaloBackground {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text("Acerca de") },
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
                    .padding(horizontal = 28.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Spacer(Modifier.height(8.dp))
                BrandMark(size = 148.dp)
                Text(
                    text = "Déjalo!",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        brush = Brush.linearGradient(
                            listOf(DejaloColors.TealDeep, DejaloColors.Navy)
                        )
                    ),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Tu compañero offline para dejar de fumar",
                    style = MaterialTheme.typography.titleMedium,
                    color = DejaloColors.TealDeep,
                    textAlign = TextAlign.Center
                )

                GradientHairline(modifier = Modifier.padding(vertical = 4.dp))

                Text(
                    text = "Déjalo! te ayuda a dejar el tabaco con métricas en tiempo real " +
                        "(tiempo sin fumar, dinero ahorrado, cigarrillos evitados y vida recuperada), " +
                        "un modo emergencia ante el ansia, respiración guiada, minijuegos de distracción, " +
                        "hitos de recuperación biológica, logros y registro de recaídas sin castigo.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )

                SoftPanel {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = "PRIVACIDAD",
                            style = MaterialTheme.typography.labelLarge,
                            color = DejaloColors.TealDeep
                        )
                        Text(
                            text = "Déjalo! funciona sin cuenta y sin servidor.\nTus registros permanecen en este dispositivo.",
                            style = MaterialTheme.typography.titleMedium,
                            color = DejaloColors.Navy
                        )
                        PrivacyBullet("Sin analytics obligatorios")
                        PrivacyBullet("Sin publicidad")
                        PrivacyBullet("Sin cuenta de usuario")
                        PrivacyBullet("Sin sincronización externa")
                        PrivacyBullet("Sin envío de episodios de ansia ni recaídas a un servidor")
                        Text(
                            text = "Solo se abre el navegador si tú pulsas enlaces opcionales (GitHub o documentación). " +
                                "Si algún día hubiera telemetría, sería opt-in y quedaría claramente separada.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = DejaloColors.InkMuted
                        )
                    }
                }

                Text(
                    text = "Widgets: mantén pulsado el escritorio → Widgets → Déjalo! (resumen o ahorrado).",
                    style = MaterialTheme.typography.bodyMedium,
                    color = DejaloColors.InkMuted,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(4.dp))

                BrandOutlinedButton(
                    text = "Documentación técnica",
                    onClick = onDocs,
                    modifier = Modifier.fillMaxWidth()
                )
                BrandOutlinedButton(
                    text = "Proyecto en GitHub",
                    onClick = {
                        runCatching {
                            context.startActivity(
                                Intent(Intent.ACTION_VIEW, Uri.parse(DEJALO_GITHUB_URL))
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "Versión 1.0.0 · Android 8.0+",
                    style = MaterialTheme.typography.labelLarge,
                    color = DejaloColors.InkMuted,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun PrivacyBullet(text: String) {
    Text(
        text = "· $text",
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurface
    )
}
