package com.moehr.habit_3.data.model.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.moehr.habit_3.data.model.entity.HabitLogEntry
import kotlinx.coroutines.flow.Flow

/**
 * Dao for database access on the habit_log_entry table
 * */
@Dao
interface HabitLogEntryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: HabitLogEntry): Long

    @Query("SELECT * FROM habit_log_entry")
    fun getAll() : Flow<List<HabitLogEntry>>

    @Query("SELECT * FROM habit_log_entry")
    suspend fun getAllStatic() : List<HabitLogEntry>

    @Query("SELECT * FROM habit_log_entry WHERE uid_habit LIKE :uid")
    suspend fun getAllByHabitUid(uid: Int): List<HabitLogEntry>

    @Delete
    suspend fun delete(item: HabitLogEntry)
}