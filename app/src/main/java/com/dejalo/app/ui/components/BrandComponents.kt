package com.dejalo.app.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dejalo.app.R
import com.dejalo.app.ui.theme.DejaloColors

const val DEJALO_GITHUB_URL = "https://github.com/entreunosyceros/dejalo"
const val DEJALO_GITHUB_SPECS_URL =
    "https://github.com/entreunosyceros/dejalo/blob/main/docs/especificaciones_tecnicas.md"

@Composable
fun BrandMark(
    size: Dp = 120.dp,
    modifier: Modifier = Modifier,
    openGithubOnClick: Boolean = true
) {
    val context = LocalContext.current
    Image(
        painter = painterResource(R.drawable.logo_dejalo),
        contentDescription = "Déjalo! — abrir proyecto en GitHub",
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(size * 0.18f))
            .then(
                if (openGithubOnClick) {
                    Modifier.clickable {
                        runCatching {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(DEJALO_GITHUB_URL))
                            context.startActivity(intent)
                        }
                    }
                } else {
                    Modifier
                }
            ),
        contentScale = ContentScale.Fit
    )
}

@Composable
fun BrandWordmark(
    subtitle: String? = null,
    centered: Boolean = false,
    logoSize: Dp = 56.dp,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                runCatching {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(DEJALO_GITHUB_URL))
                    context.startActivity(intent)
                }
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = if (centered) Arrangement.Center else Arrangement.Start
    ) {
        BrandMark(size = logoSize, openGithubOnClick = false)
        Spacer(Modifier.width(14.dp))
        Column(horizontalAlignment = if (centered) Alignment.CenterHorizontally else Alignment.Start) {
            Text(
                text = "Déjalo!",
                style = MaterialTheme.typography.headlineMedium.copy(
                    brush = Brush.linearGradient(
                        listOf(DejaloColors.TealDeep, DejaloColors.Navy)
                    )
                )
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun SectionTitle(
    title: String,
    supporting: String? = null,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = title.uppercase(),
            style = MaterialTheme.typography.labelLarge,
            color = DejaloColors.TealDeep
        )
        if (supporting != null) {
            Spacer(Modifier.height(4.dp))
            Text(
                text = supporting,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun HeroMetric(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    valueStyle: TextStyle = MaterialTheme.typography.displayMedium,
    valueColor: androidx.compose.ui.graphics.Color = DejaloColors.Navy
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelLarge,
            color = DejaloColors.TealDeep,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(6.dp))
        AnimatedContent(
            targetState = value,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "heroMetric"
        ) { text ->
            Text(
                text = text,
                style = valueStyle,
                color = valueColor,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun StatPair(
    leftLabel: String,
    leftValue: String,
    rightLabel: String,
    rightValue: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = leftLabel.uppercase(),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(4.dp))
            AnimatedContent(
                targetState = leftValue,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "leftStat"
            ) { text ->
                Text(text = text, style = MaterialTheme.typography.headlineMedium, color = DejaloColors.Navy)
            }
        }
        Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
            Text(
                text = rightLabel.uppercase(),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(4.dp))
            AnimatedContent(
                targetState = rightValue,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "rightStat"
            ) { text ->
                Text(text = text, style = MaterialTheme.typography.headlineMedium, color = DejaloColors.Navy)
            }
        }
    }
}
