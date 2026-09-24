package com.dejalo.app.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import com.dejalo.app.DejaloApp
import com.dejalo.app.MainActivity
import com.dejalo.app.R
import com.dejalo.app.domain.LiveQuitStats
import com.dejalo.app.ui.util.formatEuros
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

internal fun loadWidgetStats(context: Context): LiveQuitStats? = runCatching {
    val app = context.applicationContext as? DejaloApp ?: return null
    runBlocking {
        val profile = app.repository.observeProfile().first() ?: return@runBlocking null
        val relapses = app.repository.observeRelapses().first()
        LiveQuitStats.from(
            quitAtMillis = profile.quitAtMillis,
            nowMillis = System.currentTimeMillis(),
            cigarettesPerDay = profile.cigarettesPerDay,
            cigarettesPerPack = profile.cigarettesPerPack,
            packPriceEuros = profile.packPriceEuros,
            relapsedCigarettes = relapses.sumOf { it.cigarettes },
            relapseAtMillis = relapses.map { it.occurredAtMillis },
            savingsGoalEuros = profile.savingsGoalEuros
        )
    }
}.getOrNull()

private fun pendingFlags(): Int {
    var flags = PendingIntent.FLAG_UPDATE_CURRENT
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        flags = flags or PendingIntent.FLAG_IMMUTABLE
    }
    return flags
}

internal fun openAppPendingIntent(context: Context, requestCode: Int): PendingIntent {
    val intent = Intent(context, MainActivity::class.java).apply {
        action = Intent.ACTION_MAIN
        addCategory(Intent.CATEGORY_LAUNCHER)
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
    }
    return PendingIntent.getActivity(context, requestCode, intent, pendingFlags())
}

internal fun openEmergencyPendingIntent(context: Context, requestCode: Int): PendingIntent {
    val intent = Intent(context, MainActivity::class.java).apply {
        action = "com.dejalo.app.OPEN_EMERGENCY"
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
    }
    return PendingIntent.getActivity(context, requestCode, intent, pendingFlags())
}

internal fun buildSummaryViews(context: Context, stats: LiveQuitStats?): RemoteViews {
    val views = RemoteViews(context.packageName, R.layout.widget_summary)
    views.setOnClickPendingIntent(
        R.id.widget_summary_root,
        openAppPendingIntent(context, 1001)
    )
    views.setOnClickPendingIntent(
        R.id.widget_emergency,
        openEmergencyPendingIntent(context, 1002)
    )
    if (stats == null) {
        views.setTextViewText(R.id.widget_saved_label, "")
        views.setTextViewText(R.id.widget_saved_value, "Completa la configuración")
        views.setTextViewText(R.id.widget_streak_line, "")
        views.setViewVisibility(R.id.widget_emergency, android.view.View.GONE)
    } else {
        views.setTextViewText(R.id.widget_saved_label, "AHORRADO")
        views.setTextViewText(R.id.widget_saved_value, formatEuros(stats.moneySavedEuros))
        views.setTextViewText(
            R.id.widget_streak_line,
            "Racha ${stats.streakDays} d · Acumulado ${stats.days} d"
        )
        views.setViewVisibility(R.id.widget_emergency, android.view.View.VISIBLE)
    }
    return views
}

internal fun buildSavingsViews(context: Context, stats: LiveQuitStats?): RemoteViews {
    val views = RemoteViews(context.packageName, R.layout.widget_savings)
    views.setOnClickPendingIntent(
        R.id.widget_savings_root,
        openAppPendingIntent(context, 2001)
    )
    views.setTextViewText(
        R.id.widget_savings_value,
        if (stats == null) "—" else formatEuros(stats.moneySavedEuros)
    )
    return views
}

class DejaloWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        val stats = loadWidgetStats(context)
        val views = buildSummaryViews(context, stats)
        appWidgetIds.forEach { id -> appWidgetManager.updateAppWidget(id, views) }
    }

    companion object {
        fun updateAll(context: Context) {
            val manager = AppWidgetManager.getInstance(context)
            val ids = manager.getAppWidgetIds(
                ComponentName(context, DejaloWidgetProvider::class.java)
            )
            if (ids.isEmpty()) return
            DejaloWidgetProvider().onUpdate(context, manager, ids)
        }
    }
}

class DejaloSavingsWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        val stats = loadWidgetStats(context)
        val views = buildSavingsViews(context, stats)
        appWidgetIds.forEach { id -> appWidgetManager.updateAppWidget(id, views) }
    }

    companion object {
        fun updateAll(context: Context) {
            val manager = AppWidgetManager.getInstance(context)
            val ids = manager.getAppWidgetIds(
                ComponentName(context, DejaloSavingsWidgetProvider::class.java)
            )
            if (ids.isEmpty()) return
            DejaloSavingsWidgetProvider().onUpdate(context, manager, ids)
        }
    }
}
