package com.moehr.habit_3.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.moehr.habit_3.MainApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

/**
 * Class for receiving an intent signaling, that the device booted successfully and rescheduling
 * the notifications for all habits.
 *
 * `runBlocking {...}` is ok, because the app is in background, no UI interaction would
 * be blocked and the DB is also not accessed on the main thread.
 * */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (Intent.ACTION_BOOT_COMPLETED == intent?.action || Intent.ACTION_MY_PACKAGE_REPLACED == intent?.action) {
            val app = context.applicationContext as MainApplication
            val habitRepository = app.habitRepository
            val alarmManager = NotificationAlarmManager(context)

            runBlocking {
                withContext(Dispatchers.IO) {
                    habitRepository.getHabitsStatic().forEach { habit ->
                        alarmManager.scheduleNotificationAlarm(habit)
                        Log.d("NOTIFICATIONS", "SET UP NOTIFICATION DONE")
                    }
                }
            }
        }
    }

}