package com.moehr.habit_3.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class HabitActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        when (action) {
            "ACTION_DID_DO_IT" -> {
                Log.d("HabitActionReceiver", "ACTION_DID_DO_IT received")
            }
            "ACTION_DID_NOT_DO_IT" -> {
                Log.d("HabitActionReceiver", "ACTION_DID_NOT_DO_IT received")
            }
        }
    }
}