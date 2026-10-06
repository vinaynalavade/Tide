package com.tide.app

import android.app.Application
import com.tide.app.core.notification.TideNotificationManager
import com.tide.app.core.update.InAppUpdateManager
import com.tide.app.data.local.TideDatabase
import com.tide.app.data.repository.TideRepository
import com.tide.app.data.repository.TideRepositoryImpl

class TideApplication : Application() {

    lateinit var database: TideDatabase
        private set

    lateinit var repository: TideRepository
        private set

    lateinit var inAppUpdateManager: InAppUpdateManager
        private set

    override fun onCreate() {
        super.onCreate()
        database = TideDatabase.getInstance(this)
        repository = TideRepositoryImpl(database)
        inAppUpdateManager = InAppUpdateManager(this)

        // Initialize Notification Channels
        TideNotificationManager.createNotificationChannels(this)
    }
}
