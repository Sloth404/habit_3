package com.moehr.habit_3.data.repository

import androidx.annotation.WorkerThread
import com.moehr.habit_3.data.model.dao.HabitLogEntryDao
import com.moehr.habit_3.data.model.entity.HabitLogEntry

class HabitLogEntryRepository(private val habitLogEntryDao : HabitLogEntryDao) {
    @WorkerThread
    suspend fun create(item : HabitLogEntry) : Int {
        val uid = habitLogEntryDao.insert(item)
        return uid.toInt()
    }

    @WorkerThread
    suspend fun readAllByHabitUid(habitUid : Int) : List<HabitLogEntry> {
        return habitLogEntryDao.getAllByHabitUid(habitUid)
    }

    @WorkerThread
    suspend fun delete(item : HabitLogEntry) {
        habitLogEntryDao.delete(item)
    }
}