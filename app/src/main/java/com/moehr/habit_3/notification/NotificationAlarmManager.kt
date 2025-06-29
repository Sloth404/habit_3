package com.moehr.habit_3.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.moehr.habit_3.data.model.Habit
import com.moehr.habit_3.data.model.RepeatPattern
import com.moehr.habit_3.data.preferences.PushNotificationKeys
import com.moehr.habit_3.data.preferences.SharedPreferencesManager
import java.util.Calendar

/**
 * Sets timed intents to trigger scheduled push notifications via [NotificationAlarmReciever]
 * */
class NotificationAlarmManager(
    val context: Context
) {
    fun scheduleNotificationAlarm(habit: Habit) {
        if (habit.repeat == RepeatPattern.DAILY && habit.reminder?.isNotEmpty() == true) {
            setNextDayAlarm(habit)
        } else if (habit.repeat == RepeatPattern.WEEKLY && habit.reminder?.isNotEmpty() == true) {
            if (!habit.isThisWeekSuccessful()) {
                setNextDayAlarm(habit)
            }
        } else {
            return
        }

    }

    private fun setNextDayAlarm(habit: Habit) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        if (alarmManager.canScheduleExactAlarms()) {
            val intent = Intent(context, NotificationAlarmReciever::class.java).apply {
                action = ACTION_PUSH_REMINDER
            }.putExtra("habit_id", habit.id)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                habit.id.toInt(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            var time = SharedPreferencesManager.loadSpecificPushSetting(context, habit.reminder ?: "")
            // use the default times if the user has not set any custom values in the settings
            if ( habit.reminder?.isNotEmpty() == true && time.isEmpty()) {
                time = when (habit.reminder) {
                    PushNotificationKeys.TIME_MORNING.id -> PushNotificationKeys.TIME_MORNING.defaultTime
                    PushNotificationKeys.TIME_NOON.id -> PushNotificationKeys.TIME_NOON.defaultTime
                    PushNotificationKeys.TIME_EVENING.id -> PushNotificationKeys.TIME_EVENING.defaultTime
                    PushNotificationKeys.TIME_CUSTOM.id -> PushNotificationKeys.TIME_CUSTOM.defaultTime
                    else -> ""
                }
            }

            if (time.isNotEmpty()) {
                val calendar = Calendar.getInstance().apply {
                    timeInMillis = System.currentTimeMillis()
                    // Update hh:mm:ss of the calendar.
                    set(Calendar.HOUR_OF_DAY, if (time.isNotEmpty()) time.split(':')[0].toInt() else 9)
                    set(Calendar.MINUTE, if (time.isNotEmpty()) time.split(':')[1].toInt() else 0)
                    set(Calendar.SECOND, 0)
                    // If current time is past the hour of the alarm, update it to trigger the next day.
                    if (before(Calendar.getInstance())) {
                        add(Calendar.DATE, 1)
                    }

                    // For debugging - sets the notifications to trigger each full minute
                    /*
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 6)
                    set(Calendar.SECOND, 0)
                    while (before(Calendar.getInstance())) {
                        add(Calendar.MINUTE, 1)
                    }
                    */
                }

                // setExactAndAllowWhileIdle() to ensure alarms fire even in Doze mode.
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            } else {
                Log.d("ALARM_MANAGER", "Could not get the time for scheduling.")
            }
        } else {
            Log.d("ALARM_MANAGER", "Rights for setting exact alarms not given.")
        }
    }

    // TODO: add next monday alarm for weeekly habits

    companion object {
        // Intent action strings to identify which alarm type was used
        const val ACTION_PUSH_REMINDER = "ACTION_PUSH_REMINDER"
    }
}