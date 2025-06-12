package com.moehr.habit_3.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.moehr.habit_3.R

class NotificationHelper {
    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "habit_channel",
                "Habit Notification",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications for Habit^3"
            }

            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun showMotivationalNotification(context: Context) {
        val builder = NotificationCompat.Builder(context, "habit_channel")
            .setSmallIcon(R.drawable.logo_svg)
            .setContentTitle(context.getString(R.string.app_name))
            .setContentText(context.getString(R.string.motivation_text))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(1, builder.build())
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun showReminderNotification(context: Context) {
        val didDoItIntent = Intent(context, HabitActionReceiver::class.java).apply {
            action = "ACTION_DID_DO_IT"
        }

        val didDoItPendingIntent = PendingIntent.getBroadcast(
            context, 0, didDoItIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val didntDoItIntent = Intent(context, HabitActionReceiver::class.java).apply {
            action = "ACTION_DID_NOT_DO_IT"
        }

        val didntDoItPendingIntent = PendingIntent.getBroadcast(
            context, 1, didntDoItIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, "habit_channel")
            .setSmallIcon(R.drawable.logo_svg)
            .setContentTitle(context.getString(R.string.app_name))
            .setContentText(context.getString(R.string.reminder_text))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .addAction(R.drawable.ic_overview, context.getString(R.string.did_it), didDoItPendingIntent)
            .addAction(R.drawable.ic_statistics, context.getString(R.string.didnt_do_it), didntDoItPendingIntent)

        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(2, builder.build())
    }
}
