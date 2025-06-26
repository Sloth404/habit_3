package com.moehr.habit_3.data.model.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.moehr.habit_3.data.model.entity.HabitLogEntry

/**
 * don't need to be update-able
 * */
@Dao
interface HabitLogEntryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(habitLogEntry : HabitLogEntry) : Long

    @Query("SELECT * FROM habit_log_entry WHERE uid_habit LIKE :habtiUid")
    suspend fun getByHabitUid(habtiUid : Int) : List<HabitLogEntry>

    @Delete
    suspend fun delete(habitLogEntry : HabitLogEntry)
}