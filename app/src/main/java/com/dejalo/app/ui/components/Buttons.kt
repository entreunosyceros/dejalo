package com.dejalo.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.dejalo.app.ui.theme.DejaloColors

private val CtaShape = RoundedCornerShape(14.dp)

@Composable
fun BrandPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: (@Composable RowScope.() -> Unit)? = null
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = CtaShape,
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = DejaloColors.TealDeep,
            contentColor = Color.White,
            disabledContainerColor = DejaloColors.Line,
            disabledContentColor = DejaloColors.InkMuted
        ),
        modifier = modifier
            .fillMaxWidth()
            .height(54.dp)
    ) {
        if (content != null) content() else {
            Text(text, style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
fun CrisisButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        shape = CtaShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = DejaloColors.Crisis,
            contentColor = Color.White
        ),
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        Text(text, style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
fun BrandOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: (@Composable RowScope.() -> Unit)? = null
) {
    OutlinedButton(
        onClick = onClick,
        shape = CtaShape,
        border = BorderStroke(1.5.dp, DejaloColors.TealDeep),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = DejaloColors.Navy),
        modifier = modifier.height(48.dp)
    ) {
        if (content != null) content() else {
            Text(text, style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
fun GradientHairline(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(2.dp)
            .clip(RoundedCornerShape(2.dp))
            .background(DejaloColors.BrandGradient)
    )
}

@Composable
fun SoftPanel(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .border(1.dp, DejaloColors.Line, RoundedCornerShape(18.dp))
            .background(DejaloColors.Cloud.copy(alpha = 0.72f))
            .padding(18.dp)
    ) {
        content()
    }
}
