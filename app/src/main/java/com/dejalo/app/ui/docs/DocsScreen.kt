package com.dejalo.app.ui.docs

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dejalo.app.ui.components.BrandOutlinedButton
import com.dejalo.app.ui.components.DEJALO_GITHUB_SPECS_URL
import com.dejalo.app.ui.components.DejaloBackground
import com.dejalo.app.ui.components.GradientHairline
import com.dejalo.app.ui.components.SoftPanel
import com.dejalo.app.ui.theme.DejaloColors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val ASSET_PATH = "docs/especificaciones_tecnicas.md"

/** Estilos de título sin letter-spacing de labels (evita aspecto de Title Case / caps). */
private val DocH1 = TextStyle(
    fontFamily = FontFamily.SansSerif,
    fontWeight = FontWeight.Bold,
    fontSize = 24.sp,
    lineHeight = 30.sp,
    letterSpacing = 0.sp
)
private val DocH2 = TextStyle(
    fontFamily = FontFamily.SansSerif,
    fontWeight = FontWeight.Bold,
    fontSize = 20.sp,
    lineHeight = 26.sp,
    letterSpacing = 0.sp
)
private val DocH3 = TextStyle(
    fontFamily = FontFamily.SansSerif,
    fontWeight = FontWeight.SemiBold,
    fontSize = 17.sp,
    lineHeight = 24.sp,
    letterSpacing = 0.sp
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocsScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var markdown by remember { mutableStateOf<String?>(null) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        runCatching {
            withContext(Dispatchers.IO) {
                context.assets.open(ASSET_PATH).bufferedReader().use { it.readText() }
            }
        }.onSuccess { markdown = it }
            .onFailure { error = it.message ?: "No se pudo cargar la documentación." }
    }

    DejaloBackground {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text("Documentación") },
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
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Especificaciones técnicas de Déjalo!",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                BrandOutlinedButton(
                    text = "Abrir en GitHub",
                    onClick = {
                        runCatching {
                            context.startActivity(
                                Intent(Intent.ACTION_VIEW, Uri.parse(DEJALO_GITHUB_SPECS_URL))
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                when {
                    markdown == null && error == null -> {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(color = DejaloColors.TealDeep)
                        }
                    }
                    error != null -> {
                        SoftPanel {
                            Text(
                                text = error.orEmpty(),
                                style = MaterialTheme.typography.bodyLarge,
                                color = DejaloColors.Crisis
                            )
                        }
                    }
                    else -> MarkdownDocument(markdown.orEmpty())
                }

                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun MarkdownDocument(source: String) {
    val blocks = remember(source) { parseMarkdownBlocks(source) }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        blocks.forEach { block ->
            when (block) {
                is MdBlock.H1 -> Text(block.text, style = DocH1, color = DejaloColors.Navy)
                is MdBlock.H2 -> {
                    Spacer(Modifier.height(6.dp))
                    Text(block.text, style = DocH2, color = DejaloColors.Navy)
                }
                is MdBlock.H3 -> Text(block.text, style = DocH3, color = DejaloColors.Navy)
                is MdBlock.Hairline -> GradientHairline()
                is MdBlock.Bullet -> Text(
                    text = buildAnnotatedString {
                        append("· ")
                        appendInlineMarkdown(block.text)
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                is MdBlock.Numbered -> Text(
                    text = buildAnnotatedString {
                        append("${block.number}. ")
                        appendInlineMarkdown(block.text)
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                is MdBlock.Paragraph -> Text(
                    text = buildAnnotatedString { appendInlineMarkdown(block.text) },
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                is MdBlock.Code -> SoftPanel {
                    Text(
                        text = block.text,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 0.sp
                        ),
                        color = DejaloColors.Navy,
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                    )
                }
                is MdBlock.Spacer -> Spacer(Modifier.height(2.dp))
            }
        }
    }
}

private sealed class MdBlock {
    data class H1(val text: String) : MdBlock()
    data class H2(val text: String) : MdBlock()
    data class H3(val text: String) : MdBlock()
    data object Hairline : MdBlock()
    data class Bullet(val text: String) : MdBlock()
    data class Numbered(val number: String, val text: String) : MdBlock()
    data class Paragraph(val text: String) : MdBlock()
    data class Code(val text: String) : MdBlock()
    data object Spacer : MdBlock()
}

private fun parseMarkdownBlocks(source: String): List<MdBlock> {
    val out = mutableListOf<MdBlock>()
    val lines = source.lines()
    var i = 0
    var inCode = false
    val code = StringBuilder()

    while (i < lines.size) {
        val line = lines[i]
        when {
            line.trimStart().startsWith("```") -> {
                if (inCode) {
                    out += MdBlock.Code(code.toString().trimEnd())
                    code.clear()
                    inCode = false
                } else {
                    inCode = true
                }
            }
            inCode -> code.appendLine(line)
            line.trim() == "---" -> out += MdBlock.Hairline
            line.startsWith("### ") -> out += MdBlock.H3(line.removePrefix("### ").trim())
            line.startsWith("## ") -> out += MdBlock.H2(line.removePrefix("## ").trim())
            line.startsWith("# ") -> out += MdBlock.H1(line.removePrefix("# ").trim())
            line.trimStart().startsWith("* ") || line.trimStart().startsWith("- ") -> {
                val bullet = line.trimStart().removePrefix("* ").removePrefix("- ")
                out += MdBlock.Bullet(bullet)
            }
            numberedItem.matchEntire(line.trim()) != null -> {
                val m = numberedItem.matchEntire(line.trim())!!
                out += MdBlock.Numbered(m.groupValues[1], m.groupValues[2])
            }
            line.isBlank() -> out += MdBlock.Spacer
            else -> out += MdBlock.Paragraph(line.trim())
        }
        i++
    }
    if (inCode && code.isNotEmpty()) {
        out += MdBlock.Code(code.toString().trimEnd())
    }
    return out
}

private val numberedItem = Regex("""^(\d+)\.\s+(.+)$""")

private fun AnnotatedString.Builder.appendInlineMarkdown(text: String) {
    var remaining = text
    while (remaining.isNotEmpty()) {
        val bold = remaining.indexOf("**")
        val italic = remaining.indexOf('*').takeIf { it >= 0 && (bold < 0 || it < bold) }
        when {
            bold >= 0 && (italic == null || bold <= italic) -> {
                append(remaining.substring(0, bold))
                val end = remaining.indexOf("**", bold + 2)
                if (end < 0) {
                    append(remaining.substring(bold))
                    remaining = ""
                } else {
                    withStyle(SpanStyle(fontWeight = FontWeight.SemiBold, color = DejaloColors.Navy)) {
                        append(remaining.substring(bold + 2, end))
                    }
                    remaining = remaining.substring(end + 2)
                }
            }
            italic != null -> {
                append(remaining.substring(0, italic))
                val end = remaining.indexOf('*', italic + 1)
                if (end < 0) {
                    append(remaining.substring(italic))
                    remaining = ""
                } else {
                    withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                        append(remaining.substring(italic + 1, end))
                    }
                    remaining = remaining.substring(end + 1)
                }
            }
            else -> {
                append(remaining)
                remaining = ""
            }
        }
    }
}
