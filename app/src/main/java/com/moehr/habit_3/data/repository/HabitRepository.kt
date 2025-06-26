package com.moehr.habit_3.data.repository

import androidx.annotation.WorkerThread
import com.moehr.habit_3.data.model.dao.HabitDao
import com.moehr.habit_3.data.model.entity.Habit
import kotlinx.coroutines.flow.Flow

class HabitRepository(private val habitDao : HabitDao) {
    var allHabits : Flow<List<Habit>> = habitDao.getAll()

    @WorkerThread
    suspend fun create(item : Habit) : Int {
        val uid = habitDao.insert(item)
        return uid.toInt()
    }

    @WorkerThread
    suspend fun readById(uid : Int) : Habit {
        return habitDao.getById(uid)
    }

    @WorkerThread
    suspend fun update(item : Habit) {
        habitDao.update(item)
    }

    @WorkerThread
    suspend fun delete(item : Habit) {
        habitDao.delete(item)
    }
}