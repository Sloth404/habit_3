package com.moehr.habit_3.data.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.moehr.habit_3.data.model.dto.ReminderDTO

@Entity(
    tableName = "reminder",
    foreignKeys = [ForeignKey(
        entity = HabitEntity::class,
        parentColumns = ["uid"],
        childColumns = ["uid_habit"],
        // ensure all Reminders are deleted, when the referenced habit is deleted
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("uid_habit")]
)
data class Reminder (
    @PrimaryKey(autoGenerate = true) val uid : Int = 0,
    @ColumnInfo(name = "uid_habit") val uidHabit : Int,
    @ColumnInfo(name = "hour") var hour : Int,
    @ColumnInfo(name = "minute") var minute : Int
) {
    fun toDto() : ReminderDTO {
        return ReminderDTO(
            hour=hour,
            minute=minute
        )
    }
}