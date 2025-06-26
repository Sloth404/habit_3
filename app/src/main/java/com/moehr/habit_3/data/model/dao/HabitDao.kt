package com.moehr.habit_3.data.model.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.moehr.habit_3.data.model.entity.Habit
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(habit : Habit) : Long

    @Query("SELECT * FROM habit")
    fun getAll() : Flow<List<Habit>>

    @Query("SELECT * FROM habit WHERE uid LIKE :uid")
    suspend fun getByUid(uid : Int) : Habit

    @Update
    suspend fun update(habit : Habit)

    @Delete
    suspend fun delete(habit : Habit)
}