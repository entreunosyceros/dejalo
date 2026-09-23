package com.dejalo.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.dejalo.app.ui.theme.DejaloColors

/**
 * Escala 0–10 para medir la intensidad del ansia (ganas de fumar).
 */
@Composable
fun IntensityScale(
    title: String,
    value: Int,
    onChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    supporting: String? = null
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = title.uppercase(),
            style = MaterialTheme.typography.labelLarge,
            color = DejaloColors.TealDeep
        )
        if (supporting != null) {
            Text(
                text = supporting,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = "$value / 10",
            style = MaterialTheme.typography.headlineMedium,
            color = intensityColor(value),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            (0..10).forEach { n ->
                val selected = n == value
                Box(
                    modifier = Modifier
                        .size(if (selected) 30.dp else 26.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                selected -> intensityColor(n)
                                else -> DejaloColors.Cloud
                            }
                        )
                        .border(
                            width = 1.dp,
                            color = if (selected) intensityColor(n) else DejaloColors.Line,
                            shape = CircleShape
                        )
                        .clickable { onChange(n) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$n",
                        style = MaterialTheme.typography.labelMedium,
                        color = if (selected) DejaloColors.Cloud else DejaloColors.Navy
                    )
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Nada", style = MaterialTheme.typography.labelMedium, color = DejaloColors.InkMuted)
            Text("Máximo", style = MaterialTheme.typography.labelMedium, color = DejaloColors.InkMuted)
        }
        Spacer(Modifier.height(2.dp))
    }
}

fun intensityColor(value: Int) = when {
    value <= 3 -> DejaloColors.TealDeep
    value <= 6 -> DejaloColors.Teal
    value <= 8 -> DejaloColors.Crisis.copy(alpha = 0.85f)
    else -> DejaloColors.Crisis
}
