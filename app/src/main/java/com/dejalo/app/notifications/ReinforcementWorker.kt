package com.dejalo.app.notifications

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.dejalo.app.DejaloApp
import kotlinx.coroutines.flow.first
import java.util.Calendar
import java.util.concurrent.TimeUnit

class ReinforcementWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val highRisk = hour in 13..15 || hour in 10..11 || hour in 17..19
        if (!highRisk) return Result.success()

        val repo = (applicationContext as DejaloApp).repository
        val stats = repo.observeLiveStats().first()
        val days = stats?.days ?: 0

        NotificationHelper.show(
            context = applicationContext,
            channelId = NotificationHelper.CHANNEL_REINFORCEMENT,
            id = 2001,
            title = "Momento de refuerzo",
            body = if (days > 0) {
                "Llevas $days día${if (days == 1) "" else "s"} limpio. Esta franja suele ser difícil: respira y sigue."
            } else {
                "Si aparece el impulso, abre el modo emergencia. Solo son unos minutos."
            }
        )

        if (days in listOf(1, 3, 7, 14, 30)) {
            NotificationHelper.show(
                context = applicationContext,
                channelId = NotificationHelper.CHANNEL_MILESTONES,
                id = 1000 + days,
                title = "¡Hito alcanzado!",
                body = "Has cumplido $days día${if (days == 1) "" else "s"} limpio."
            )
        }

        return Result.success()
    }
}

object ReinforcementScheduler {
    private const val UNIQUE = "dejalo_reinforcement"

    fun schedule(context: Context) {
        val request = PeriodicWorkRequestBuilder<ReinforcementWorker>(6, TimeUnit.HOURS)
            .build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            UNIQUE,
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }
}
