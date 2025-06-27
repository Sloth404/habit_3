package com.moehr.habit_3

import android.app.Application
import com.moehr.habit_3.data.model.database.AppDatabase
import com.moehr.habit_3.data.repository.HabitRepository


/**
 * Custom [Application] class for global app-level initialization and dependency management.
 *
 * This class provides lazy-initialized singletons for Room database access and all
 * repositories used throughout the app. It serves as a lightweight dependency container.
 */
class MainApplication : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
    val habitRepository by lazy { HabitRepository(
        database.habitDao(),
        database.habitLogEntryDao()
    )}
}