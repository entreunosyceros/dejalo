package com.dejalo.app.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.dejalo.app.MainActivity
import com.dejalo.app.R

object NotificationHelper {
    const val CHANNEL_MILESTONES = "milestones"
    const val CHANNEL_REINFORCEMENT = "reinforcement"
    const val CHANNEL_RISK = "risk_zones"

    fun createChannels(context: Context) {
        val manager = context.getSystemService(NotificationManager::class.java) ?: return
        manager.createNotificationChannel(
            NotificationChannel(
                CHANNEL_MILESTONES,
                context.getString(R.string.channel_milestones),
                NotificationManager.IMPORTANCE_DEFAULT
            )
        )
        manager.createNotificationChannel(
            NotificationChannel(
                CHANNEL_REINFORCEMENT,
                context.getString(R.string.channel_reinforcement),
                NotificationManager.IMPORTANCE_DEFAULT
            )
        )
        manager.createNotificationChannel(
            NotificationChannel(
                CHANNEL_RISK,
                context.getString(R.string.channel_risk),
                NotificationManager.IMPORTANCE_HIGH
            )
        )
    }

    fun emergencyPendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            action = "com.dejalo.app.OPEN_EMERGENCY"
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        return PendingIntent.getActivity(
            context,
            42,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    fun show(
        context: Context,
        channelId: String,
        id: Int,
        title: String,
        body: String,
        openEmergency: Boolean = false
    ) {
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(
                if (channelId == CHANNEL_RISK) NotificationCompat.PRIORITY_HIGH
                else NotificationCompat.PRIORITY_DEFAULT
            )
            .setAutoCancel(true)

        if (openEmergency) {
            val pi = emergencyPendingIntent(context)
            builder.setContentIntent(pi)
            builder.addAction(0, "Abrir modo emergencia", pi)
        }

        try {
            NotificationManagerCompat.from(context).notify(id, builder.build())
        } catch (_: SecurityException) {
            // Permission not granted on Android 13+
        }
    }
}
