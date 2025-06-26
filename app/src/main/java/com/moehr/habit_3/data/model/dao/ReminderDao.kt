package com.moehr.habit_3.data.model.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.moehr.habit_3.data.model.entity.Reminder
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item : Reminder) : Long

    @Query("SELECT * FROM reminder")
    fun getAll() : Flow<List<Reminder>>

    @Query("SELECT * FROM reminder WHERE uid_habit LIKE :uid")
    suspend fun getAllByHabitUid(uid : Int) : List<Reminder>

    @Delete
    suspend fun delete(item : Reminder)
}