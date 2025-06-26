package com.moehr.habit_3.data.model.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.moehr.habit_3.data.model.entity.Reminder

@Dao
interface ReminderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(reminder : Reminder) : Long

    @Query("SELECT * FROM reminder WHERE uid_habit LIKE :habitUid")
    suspend fun getAllByHabitUid(habitUid : Int) : List<Reminder>

    @Delete
    suspend fun delete(reminder : Reminder)
}