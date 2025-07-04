package com.moehr.habit_3.data.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.moehr.habit_3.data.model.HabitType
import com.moehr.habit_3.data.model.RepeatPattern
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Dataclass defining the structure of the `habit` table for the database
 * */
@Entity(tableName = "habit")
data class HabitEntity (
    @PrimaryKey(autoGenerate = true) val uid : Int = 0,
    @ColumnInfo(name = "name") val name : String,
    @ColumnInfo(name = "type") val type : HabitType,
    @ColumnInfo(name = "target") val target : Int,
    @ColumnInfo(name = "unit") val unit : String,
    @ColumnInfo(name = "repeat") val repeat : RepeatPattern,
    @ColumnInfo(name = "reminder") val reminder : String?,
    @ColumnInfo(name = "created_at") val createdAt : LocalDateTime,
    @ColumnInfo(name = "motivational_note") val motivationalNote : String
)