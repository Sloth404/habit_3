package com.moehr.habit_3.notification

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.moehr.habit_3.data.model.Habit

/**
 * Recieves notification alarms from [NotificationAlarmManager] and triggers notifications via [NotificationHelper]
 * */
class NotificationAlarmReciever : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        val habit = intent.getSerializableExtra("habit", Habit::class.java)
        if (habit != null) {
            when (action) {
                NotificationAlarmManager.ACTION_PUSH_REMINDER -> {
                    // check for permission
                    val postNotificationPermission = ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS
                    )
                    if (postNotificationPermission != PackageManager.PERMISSION_GRANTED) {
                        // if permission not given don't send notification
                        return
                    } else {
                        // push the notification
                        NotificationHelper().showReminderNotification(context, habit)

                        // schedule next notification
                        NotificationAlarmManager(context).scheduleNotificationAlarm(habit)
                    }
                }
            }
        }
    }
}