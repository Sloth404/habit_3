package com.moehr.habit_3.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.moehr.habit_3.MainApplication
import com.moehr.habit_3.data.model.Habit
import com.moehr.habit_3.data.viewmodel.HabitViewModel
import kotlinx.coroutines.runBlocking
import java.time.LocalDate

/**
 * Receives broadcasted intents for updating a habit as done/not done via [HabitViewModel]
 *
 * `runBlocking {...}` is ok, because the app is in background and no UI interaction would
 * be blocked.
 * */
class HabitActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        val habit = intent.getSerializableExtra("habit", Habit::class.java)
        val app = context.applicationContext as MainApplication
        val habitRepository = app.habitRepository

        if (habit != null) {
            when (action) {
                NotificationHelper.ACTION_DID_DO_IT -> {
                    Log.d("HabitActionReceiver", "ACTION_DID_DO_IT received")
                    if (!habit.isTodaySuccessful()) {
                        val logList: MutableList<LocalDate> = habit.log.toMutableList()

                        if (habit.isTodaySuccessful()) {
                            logList.remove(LocalDate.now())
                        } else {
                            logList.add(LocalDate.now())
                        }

                        // Updates the log list
                        val updatedHabit = Habit(
                            id = habit.id,
                            name = habit.name,
                            type = habit.type,
                            target = habit.target,
                            unit = habit.unit,
                            repeat = habit.repeat,
                            reminder = habit.reminder,
                            createdAt = habit.createdAt,
                            motivationalNote = habit.motivationalNote,
                            log = logList
                        )

                        runBlocking {
                            habitRepository.updateHabit(updatedHabit)
                        }
                    }
                }
                NotificationHelper.ACTION_DID_NOT_DO_IT -> {
                    Log.d("HabitActionReceiver", "ACTION_DID_NOT_DO_IT received")
                }
            }
        }
    }
}