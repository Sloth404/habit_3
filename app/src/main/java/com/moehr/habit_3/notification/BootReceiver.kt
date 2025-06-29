package com.moehr.habit_3.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.moehr.habit_3.MainApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

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