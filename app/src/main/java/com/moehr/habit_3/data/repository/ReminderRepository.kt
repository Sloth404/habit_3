package com.moehr.habit_3.data.repository

import androidx.annotation.WorkerThread
import com.moehr.habit_3.data.model.dao.ReminderDao
import com.moehr.habit_3.data.model.entity.Reminder

class ReminderRepository(private val reminderDao : ReminderDao) {
    @WorkerThread
    suspend fun create(item : Reminder) : Int {
        val uid = reminderDao.insert(item)
        return uid.toInt()
    }

    @WorkerThread
    suspend fun readAllByHabitUid(habitUid : Int) : List<Reminder> {
        return reminderDao.getAllByHabitUid(habitUid)
    }

    @WorkerThread
    suspend fun delete(item : Reminder) {
        reminderDao.delete(item)
    }
}