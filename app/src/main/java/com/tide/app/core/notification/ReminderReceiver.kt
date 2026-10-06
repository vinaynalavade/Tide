package com.tide.app.core.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            ACTION_DAILY_REMINDER -> {
                TideNotificationManager.showDailyCheckInNotification(context)
            }
            Intent.ACTION_BOOT_COMPLETED -> {
                // Re-register daily alarm on device restart
                TideNotificationManager.scheduleDailyReminder(context, 20, 0)
            }
        }
    }

    companion object {
        const val ACTION_DAILY_REMINDER = "com.tide.app.ACTION_DAILY_REMINDER"
        const val REQUEST_CODE_DAILY = 4001
    }
}
