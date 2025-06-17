package com.moehr.habit_3.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moehr.habit_3.data.model.Habit
import com.moehr.habit_3.data.repository.HabitRepository
import kotlinx.coroutines.launch

/**
 * ViewModel responsible for managing Habit data and business logic.
 *
 * Interacts with the [HabitRepository] to perform CRUD operations on Habit entities.
 * Uses [viewModelScope] to launch coroutines for asynchronous operations.
 *
 * @property repository The repository instance used for data access.
 */
class HabitViewModel(
    private val repository: HabitRepository
) : ViewModel() {

    /**
     * LiveData list of all habits observed from the repository.
     * UI can observe this to update automatically when the data changes.
     */
    val habits: LiveData<List<Habit>> = repository.getHabits()

    /**
     * Adds a new habit asynchronously.
     *
     * @param habit The [Habit] object to add.
     */
    fun addHabit(habit: Habit) {
        viewModelScope.launch {
            repository.addHabit(habit)
        }
    }

    /**
     * Deletes an existing habit asynchronously.
     *
     * @param habit The [Habit] object to delete.
     */
    fun deleteHabit(habit: Habit) {
        viewModelScope.launch {
            repository.deleteHabit(habit)
        }
    }

    /**
     * Updates an existing habit asynchronously.
     *
     * @param habit The [Habit] object with updated data.
     */
    fun updateHabit(habit: Habit) {
        viewModelScope.launch {
            repository.updateHabit(habit)
        }
    }

    /**
     * Retrieves a habit by its ID synchronously.
     *
     * @param id The unique identifier of the habit.
     * @return The [Habit] object if found, or null otherwise.
     */
    fun getHabitById(id: Long): Habit? {
        return repository.getHabitById(id)
    }
}
