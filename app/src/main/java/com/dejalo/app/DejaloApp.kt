package com.dejalo.app

import android.app.Application
import com.dejalo.app.data.QuitRepository
import com.dejalo.app.data.local.DejaloDatabase
import com.dejalo.app.notifications.NotificationHelper
import com.dejalo.app.notifications.ReinforcementScheduler
import com.dejalo.app.notifications.RiskZoneScheduler
import com.dejalo.app.widget.WidgetUpdateWorker
import com.dejalo.app.widget.WidgetUpdater

class DejaloApp : Application() {
    lateinit var database: DejaloDatabase
        private set
    lateinit var repository: QuitRepository
        private set

    override fun onCreate() {
        super.onCreate()
        database = DejaloDatabase.get(this)
        repository = QuitRepository(database)
        NotificationHelper.createChannels(this)
        ReinforcementScheduler.schedule(this)
        RiskZoneScheduler.schedule(this)
        WidgetUpdateWorker.schedule(this)
        WidgetUpdater.enqueue(this)
    }
}

val Application.dejaloRepository: QuitRepository
    get() = (this as DejaloApp).repository
