package com.moehr.habit_3.data.model

import com.moehr.habit_3.data.model.dto.HabitLogEntryDTO
import com.moehr.habit_3.data.model.dto.ReminderTimeDTO
import java.io.Serializable
import java.time.LocalDate
import java.time.LocalDateTime

data class Habit(
    val id: Long,
    val name: String,
    val type: HabitType,
    val target: Int,
    val unit: String,
    val repeat: RepeatPattern,
    val reminders: List<ReminderTimeDTO>,
    val createdAt: LocalDateTime,
    val motivationalNote: String,
    val log: List<HabitLogEntryDTO>
) : Serializable {
    fun getCurrentStreak(): Int = log.count { it.success }

    fun getSuccessfulDates(): List<LocalDate> = log.filter { it.success }.map { it.date.toLocalDate() }
}