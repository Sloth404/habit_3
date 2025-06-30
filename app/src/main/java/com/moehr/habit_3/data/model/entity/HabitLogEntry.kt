package com.moehr.habit_3.data.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(
    tableName = "habit_log_entry",
    foreignKeys = [ForeignKey(
        entity = HabitEntity::class,
        parentColumns = ["uid"],
        childColumns = ["uid_habit"],
        // ensure all habit log entries are deleted, when the referenced habit is deleted
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("uid_habit")]
)
data class HabitLogEntry(
    @PrimaryKey(autoGenerate = true) val uid : Int = 0,
    @ColumnInfo(name = "uid_habit") var uidHabit : Int,
    @ColumnInfo(name = "date") var date : LocalDate,
)
