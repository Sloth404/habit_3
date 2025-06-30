package com.moehr.habit_3.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.moehr.habit_3.data.repository.HabitRepository

/**
 * Factory class responsible for creating instances of [HabitViewModel].
 *
 * This factory enables passing the [HabitRepository] dependency into the [HabitViewModel].
 *
 * @property repository The [HabitRepository] instance to be provided to the ViewModel.
 */
class HabitViewModelFactory(
    private val repository: HabitRepository
) : ViewModelProvider.Factory {

    /**
     * Creates a new instance of the given [ViewModel] class.
     *
     * @param modelClass The class of the ViewModel to create.
     * @return A new instance of the requested ViewModel.
     * @throws IllegalArgumentException if the ViewModel class is unknown.
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Check if the requested ViewModel is of type HabitViewModel
        if (modelClass.isAssignableFrom(HabitViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            // Return a new HabitViewModel with the repository injected
            return HabitViewModel(repository) as T
        }
        // Throw an exception if an unsupported ViewModel class is requested
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
