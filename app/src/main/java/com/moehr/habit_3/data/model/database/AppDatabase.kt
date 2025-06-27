package com.moehr.habit_3.data.model.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.moehr.habit_3.data.model.dao.HabitDao
import com.moehr.habit_3.data.model.dao.HabitLogEntryDao
import com.moehr.habit_3.data.model.entity.HabitEntity
import com.moehr.habit_3.data.model.entity.HabitLogEntry

/**
 * The main Room database for the app.
 *
 * This abstract class defines the database configuration and serves as the app’s
 * main access point to persisted data. It includes DAOs for [Todo], [Category],
 * [TodoCategory], and [Priority] entities.
 *
 * Room automatically generates the implementation of this class at compile time.
 *
 * @see RoomDatabase
 */
@Database(entities = [HabitEntity::class, HabitLogEntry::class], version = 2)
@TypeConverters(Converters::class)
abstract class  AppDatabase : RoomDatabase() {
    abstract fun habitDao() : HabitDao
    abstract fun habitLogEntryDao() : HabitLogEntryDao

    companion object {
        @Volatile
        private var INSTANCE : AppDatabase? = null

        /**
         * (Explained by ChatGPT)
         * Returns the singleton instance of [AppDatabase].
         *
         * If the database has not been created yet, it initializes it using the application context.
         * The use of `synchronized` ensures thread safety.
         *
         * This method also enables `.fallbackToDestructiveMigration(true)`, which means that
         * if no migration is provided during a version upgrade, Room will wipe and rebuild the database.
         * **Warning:** This deletes all existing data on schema changes.
         *
         * @param context The application context.
         * @return The singleton instance of [AppDatabase].
         */
        fun getDatabase(context : Context) : AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .fallbackToDestructiveMigration(true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}