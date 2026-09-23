package com.dejalo.app.widget

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.dejalo.app.DejaloApp
import com.dejalo.app.MainActivity
import com.dejalo.app.domain.LiveQuitStats
import com.dejalo.app.ui.util.formatEuros
import kotlinx.coroutines.flow.first

private val Navy = ColorProvider(android.graphics.Color.parseColor("#0F3D2E"))
private val Lime = ColorProvider(android.graphics.Color.parseColor("#7BC49A"))
private val White = ColorProvider(android.graphics.Color.WHITE)
private val Crisis = ColorProvider(android.graphics.Color.parseColor("#C45C26"))

internal suspend fun loadWidgetStats(context: Context): LiveQuitStats? {
    val app = context.applicationContext as DejaloApp
    val profile = app.repository.observeProfile().first() ?: return null
    val relapses = app.repository.observeRelapses().first()
    val now = System.currentTimeMillis()
    return LiveQuitStats.from(
        quitAtMillis = profile.quitAtMillis,
        nowMillis = now,
        cigarettesPerDay = profile.cigarettesPerDay,
        cigarettesPerPack = profile.cigarettesPerPack,
        packPriceEuros = profile.packPriceEuros,
        relapsedCigarettes = relapses.sumOf { it.cigarettes },
        relapseAtMillis = relapses.map { it.occurredAtMillis },
        savingsGoalEuros = profile.savingsGoalEuros
    )
}

/** Widget resumen: ahorro destacado + días + acceso a emergencia. */
class DejaloWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val stats = loadWidgetStats(context)
        provideContent {
            GlanceTheme {
                SummaryWidgetContent(context.packageName, stats)
            }
        }
    }
}

@Composable
private fun SummaryWidgetContent(packageName: String, stats: LiveQuitStats?) {
    val openApp = Intent(Intent.ACTION_MAIN).setClassName(packageName, MainActivity::class.java.name)
    val openEmergency = Intent(Intent.ACTION_VIEW)
        .setClassName(packageName, MainActivity::class.java.name)
        .setAction("com.dejalo.app.OPEN_EMERGENCY")

    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(Navy)
            .padding(16.dp)
            .clickable(actionStartActivity(openApp)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Déjalo!",
            style = TextStyle(color = White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        )
        Spacer(GlanceModifier.height(6.dp))
        if (stats == null) {
            Text(
                text = "Completa la configuración",
                style = TextStyle(color = White, fontSize = 14.sp)
            )
        } else {
            Text(
                text = "AHORRADO",
                style = TextStyle(color = Lime, fontWeight = FontWeight.Medium, fontSize = 11.sp)
            )
            Text(
                text = formatEuros(stats.moneySavedEuros),
                style = TextStyle(color = White, fontWeight = FontWeight.Bold, fontSize = 26.sp)
            )
            Spacer(GlanceModifier.height(4.dp))
            Text(
                text = "Racha ${stats.streakDays} d · Acumulado ${stats.days} d",
                style = TextStyle(color = Lime, fontSize = 13.sp)
            )
            Spacer(GlanceModifier.height(10.dp))
            Text(
                text = "Modo emergencia",
                style = TextStyle(color = White, fontWeight = FontWeight.Bold, fontSize = 13.sp),
                modifier = GlanceModifier
                    .fillMaxWidth()
                    .background(Crisis)
                    .padding(10.dp)
                    .clickable(actionStartActivity(openEmergency))
            )
        }
    }
}

class DejaloWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = DejaloWidget()
}

/** Widget compacto centrado solo en el dinero ahorrado. */
class DejaloSavingsWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val stats = loadWidgetStats(context)
        provideContent {
            GlanceTheme {
                SavingsWidgetContent(context.packageName, stats)
            }
        }
    }
}

@Composable
private fun SavingsWidgetContent(packageName: String, stats: LiveQuitStats?) {
    val openApp = Intent(Intent.ACTION_MAIN).setClassName(packageName, MainActivity::class.java.name)

    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(Navy)
            .padding(14.dp)
            .clickable(actionStartActivity(openApp)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Ahorrado",
            style = TextStyle(color = Lime, fontWeight = FontWeight.Medium, fontSize = 12.sp)
        )
        Spacer(GlanceModifier.height(4.dp))
        if (stats == null) {
            Text(
                text = "—",
                style = TextStyle(color = White, fontWeight = FontWeight.Bold, fontSize = 22.sp)
            )
        } else {
            Text(
                text = formatEuros(stats.moneySavedEuros),
                style = TextStyle(color = White, fontWeight = FontWeight.Bold, fontSize = 24.sp)
            )
        }
        Spacer(GlanceModifier.height(2.dp))
        Text(
            text = "Déjalo!",
            style = TextStyle(color = Lime, fontSize = 11.sp)
        )
    }
}

class DejaloSavingsWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = DejaloSavingsWidget()
}
