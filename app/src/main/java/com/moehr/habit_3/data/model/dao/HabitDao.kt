package com.moehr.habit_3.data.model.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.moehr.habit_3.data.model.entity.HabitEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(habitEntity : HabitEntity) : Long

    @Query("SELECT * FROM habit")
    fun getAll() : Flow<List<HabitEntity>>

    @Query("SELECT * FROM habit WHERE uid LIKE :uid")
    suspend fun getById(uid : Int) : HabitEntity?

    @Update
    suspend fun update(habitEntity : HabitEntity)

    @Delete
    suspend fun delete(habitEntity : HabitEntity)
}