package com.moehr.habit_3.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.moehr.habit_3.R
import com.moehr.habit_3.data.model.Habit

/**
 * Helper class to manage notification channels and display notifications related to habits.
 */
class NotificationHelper {

    /**
     * Creates a notification channel on devices running Android O (API 26) or higher.
     * This is required to send notifications on newer Android versions.
     *
     * @param context Application context used to access system services.
     */
    fun createNotificationChannel(context: Context) {
        // Define the notification channel with ID, name, and importance level
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Habit Notification",
            NotificationManager.IMPORTANCE_HIGH // not DEFAULT -> notifications wakeup the device
        ).apply {
            description = "Notifications for Habit^3"
        }

        // Register the channel with the system notification manager
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    /**
     * Displays a motivational notification.
     *
     * Requires POST_NOTIFICATIONS permission on Android 13+.
     *
     * @param context Application context to build and show the notification.
     */
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun showMotivationalNotification(context: Context, habit: Habit) {
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.logo_svg)
            .setContentTitle(context.getString(R.string.app_name))
            .setContentText("${context.getString(R.string.motivation_text)}${habit.name}")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        NotificationManagerCompat.from(context).notify(MOTIVATION_NOTIFICATION_ID, builder.build())

    }

    /**
     * Displays a reminder notification with actionable buttons for user interaction.
     *
     * Requires POST_NOTIFICATIONS permission on Android 13+.
     *
     * @param context Application context used to build and show the notification.
     */
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun showReminderNotification(
        context: Context,
        habit: Habit
    ) {
        // Intent triggered when user indicates they did the habit
        val didDoItIntent = Intent(context, HabitActionReceiver::class.java).apply {
            action = ACTION_DID_DO_IT
        }.putExtra("habit", habit)
        val didDoItPendingIntent = PendingIntent.getBroadcast(
            context, 0, didDoItIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Intent triggered when user indicates they didn't do the habit
        val didntDoItIntent = Intent(context, HabitActionReceiver::class.java).apply {
            action = ACTION_DID_NOT_DO_IT
        }.putExtra("habit", habit)
        val didntDoItPendingIntent = PendingIntent.getBroadcast(
            context, 1, didntDoItIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Build notification with two action buttons
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.logo_svg)
            .setContentTitle(context.getString(R.string.app_name))
            .setContentText("${context.getString(R.string.reminder_text)}\n${habit.name}")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .addAction(
                R.drawable.ic_overview,
                context.getString(R.string.did_it),
                didDoItPendingIntent
            )
            .addAction(
                R.drawable.ic_statistics,
                context.getString(R.string.didnt_do_it),
                didntDoItPendingIntent
            )

        wakeUpDevice(context)
        NotificationManagerCompat.from(context).notify(REMINDER_NOTIFICATION_ID, builder.build())
    }

    /**
     * https://stackoverflow.com/questions/77159747/how-to-turn-on-the-screen-when-notification-arrives-on-device
     */
    private fun wakeUpDevice(context : Context) {
        val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val screenIsOn = pm.isInteractive

        if (!screenIsOn) {
            val wakeLockTag = "habit3:notification_WAKELOCK"
            // left the deprecated
            val wakeLock = pm.newWakeLock(
                PowerManager.ON_AFTER_RELEASE, wakeLockTag
            )
            // acquire will turn on the display - autorelease adter timeout
            wakeLock.acquire(3000)
        }
    }

    companion object {
        // Notification channel ID used for all habit notifications
        private const val CHANNEL_ID = "habit_channel"

        // Notification IDs to distinguish different notifications
        internal const val MOTIVATION_NOTIFICATION_ID = 1
        internal const val REMINDER_NOTIFICATION_ID = 2

        // Intent action strings to identify user responses
        const val ACTION_DID_DO_IT = "ACTION_DID_DO_IT"
        const val ACTION_DID_NOT_DO_IT = "ACTION_DID_NOT_DO_IT"
    }
}
