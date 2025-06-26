package com.moehr.habit_3.data.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.moehr.habit_3.data.model.HabitType
import com.moehr.habit_3.data.model.RepeatPattern
import java.time.LocalDateTime

@Entity(tableName = "habit")
data class Habit (
    @PrimaryKey(autoGenerate = true) val uid : Int = 0,
    @ColumnInfo(name = "name") val name : String,
    @ColumnInfo(name = "type") val type : HabitType,
    @ColumnInfo(name = "target") val target : Int,
    @ColumnInfo(name = "unit") val unit : String,
    @ColumnInfo(name = "repeat") val repeat : RepeatPattern,
    @ColumnInfo(name = "created_at") val createdAt : LocalDateTime,
    @ColumnInfo(name = "motivational_note") val motivationalNote : String
)