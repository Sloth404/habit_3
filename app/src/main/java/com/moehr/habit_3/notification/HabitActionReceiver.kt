package com.moehr.habit_3.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.moehr.habit_3.data.viewmodel.HabitViewModel

/**
 * Receives broadcasted intents for updating a habit as done/not done via [HabitViewModel]
 * */
class HabitActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        val extras = intent.extras
        if (extras != null) {
            when (action) {
                NotificationHelper.ACTION_DID_DO_IT -> {
                    Log.d("HabitActionReceiver", "ACTION_DID_DO_IT received")
                }
                NotificationHelper.ACTION_DID_NOT_DO_IT -> {
                    Log.d("HabitActionReceiver", "ACTION_DID_NOT_DO_IT received")
                }
            }
        }
    }
}