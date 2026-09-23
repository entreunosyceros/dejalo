package com.dejalo.app.notifications

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.dejalo.app.DejaloApp
import com.dejalo.app.domain.craving.CravingRiskDetector
import com.dejalo.app.domain.craving.RiskPhase
import kotlinx.coroutines.flow.first
import java.util.Calendar
import java.util.concurrent.TimeUnit

/**
 * Revisa cada hora si nos acercamos a una zona de riesgo personalizada
 * (según el historial de ansia) y avisa con acceso al modo emergencia.
 */
class RiskZoneWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val repo = (applicationContext as DejaloApp).repository
        val events = repo.observeCravings().first()
        val alert = CravingRiskDetector.alertForNow(events) ?: return Result.success()

        val prefs = applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val dayKey = Calendar.getInstance().let {
            "${it.get(Calendar.YEAR)}-${it.get(Calendar.DAY_OF_YEAR)}"
        }
        val zoneKey = "${alert.zone.startHour}-${alert.zone.endHour}"
        val notifyKey = "risk_${dayKey}_${zoneKey}_${alert.phase.name}"
        if (prefs.getBoolean(notifyKey, false)) return Result.success()

        // Evitar spam: como máximo un aviso APPROACHING y uno ACTIVE por zona/día
        NotificationHelper.show(
            context = applicationContext,
            channelId = NotificationHelper.CHANNEL_RISK,
            id = 3000 + alert.zone.startHour,
            title = alert.title,
            body = alert.body,
            openEmergency = true
        )
        prefs.edit().putBoolean(notifyKey, true).apply()

        // Limpiar claves antiguas (días previos)
        if (alert.phase == RiskPhase.ACTIVE) {
            prefs.all.keys
                .filter { it.startsWith("risk_") && !it.contains(dayKey) }
                .take(20)
                .forEach { prefs.edit().remove(it).apply() }
        }

        return Result.success()
    }

    companion object {
        private const val PREFS = "dejalo_risk_notify"
    }
}

object RiskZoneScheduler {
    private const val UNIQUE = "dejalo_risk_zones"

    fun schedule(context: Context) {
        val request = PeriodicWorkRequestBuilder<RiskZoneWorker>(1, TimeUnit.HOURS)
            .build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            UNIQUE,
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }
}
