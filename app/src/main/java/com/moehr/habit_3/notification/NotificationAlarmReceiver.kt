package com.moehr.habit_3.notification

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.moehr.habit_3.MainApplication
import com.moehr.habit_3.data.model.Habit
import kotlinx.coroutines.runBlocking

/**
 * Recieves notification alarms from [NotificationAlarmManager] and triggers notifications via [NotificationHelper].
 *
 * `runBlocking {...}` is ok, because the app is in background and no UI interaction would
 * be blocked.
 * */
class NotificationAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        val habitId = intent.getLongExtra("habit_id", -1)
        val app = context.applicationContext as MainApplication
        val habitRepository = app.habitRepository

        if (habitId == -1L) return
        val habit: Habit? = runBlocking {
            habitRepository.getHabitById(habitId)
        }

        // Ensure no notification will be sent for nonexistent habits
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