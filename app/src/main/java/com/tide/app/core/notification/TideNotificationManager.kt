package com.tide.app.core.notification

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.tide.app.MainActivity
import com.tide.app.R
import java.util.Calendar

object TideNotificationManager {

    const val CHANNEL_CHECK_IN = "tide_daily_checkin"
    const val CHANNEL_GOALS = "tide_goal_reminders"
    const val CHANNEL_MILESTONES = "tide_milestones"

    const val NOTIFICATION_ID_CHECK_IN = 1001
    const val NOTIFICATION_ID_MILESTONE = 1002

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val checkInChannel = NotificationChannel(
                CHANNEL_CHECK_IN,
                "Daily Check-In",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Gentle daily check-in reminders"
            }

            val goalChannel = NotificationChannel(
                CHANNEL_GOALS,
                "Goal Reminders",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Daily progress encouragement"
            }

            val milestoneChannel = NotificationChannel(
                CHANNEL_MILESTONES,
                "Milestones & Achievements",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Celebrations of your recovery progress"
            }

            notificationManager.createNotificationChannels(listOf(checkInChannel, goalChannel, milestoneChannel))
        }
    }

    fun showDailyCheckInNotification(context: Context) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_CHECK_IN)
            .setSmallIcon(R.drawable.ic_stat_tide)
            .setContentTitle("Stay with the tide")
            .setContentText("How is today going? Take 10 seconds to check in.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_CHECK_IN, notification)
        } catch (_: SecurityException) {}
    }

    fun showMilestoneNotification(context: Context, title: String, message: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            1,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_MILESTONES)
            .setSmallIcon(R.drawable.ic_stat_tide)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_MILESTONE, notification)
        } catch (_: SecurityException) {}
    }

    fun scheduleDailyReminder(context: Context, hour: Int = 20, minute: Int = 0) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            action = ReminderReceiver.ACTION_DAILY_REMINDER
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            ReminderReceiver.REQUEST_CODE_DAILY,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            if (before(Calendar.getInstance())) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        try {
            alarmManager.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                pendingIntent
            )
        } catch (_: Exception) {}
    }

    fun cancelDailyReminder(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            action = ReminderReceiver.ACTION_DAILY_REMINDER
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            ReminderReceiver.REQUEST_CODE_DAILY,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
        }
    }
}
