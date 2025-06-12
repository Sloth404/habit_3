package com.moehr.habit_3.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.moehr.habit_3.data.model.Habit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HabitRepository {
    private val habitList = mutableListOf<Habit>()

    private val habitsLiveData = MutableLiveData<List<Habit>>(habitList)

    fun getHabits(): LiveData<List<Habit>> {
        return habitsLiveData
    }

    suspend fun addHabit(habit: Habit) {
        withContext(Dispatchers.IO) {
            habitList.add(habit)
            habitsLiveData.postValue(habitList.toList())
        }
    }

    suspend fun deleteHabit(habit: Habit) {
        withContext(Dispatchers.IO) {
            habitList.remove(habit)
            habitsLiveData.postValue(habitList.toList())
        }
    }

    suspend fun updateHabit(updatedHabit: Habit) {
        withContext(Dispatchers.IO) {
            val index = habitList.indexOfFirst { it.id == updatedHabit.id }
            if (index != -1) {
                habitList[index] = updatedHabit
                habitsLiveData.postValue(habitList.toList())
            }
        }
    }

    fun getHabitById(id: Long): Habit? {
        return habitList.find { it.id == id }
    }
}