package com.dejalo.app.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.dejalo.app.widget.WidgetUpdater

class BootCompletedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            ReinforcementScheduler.schedule(context.applicationContext)
            RiskZoneScheduler.schedule(context.applicationContext)
            WidgetUpdater.enqueue(context.applicationContext)
        }
    }
}
