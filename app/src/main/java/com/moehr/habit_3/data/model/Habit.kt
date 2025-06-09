package com.moehr.habit_3.data.model

import java.time.LocalDateTime

data class Habit(
    val id: Long,
    val name: String,
    val type: HabitType,
    val unit: String,
    val repeat: RepeatPattern,
    val reminders: List<ReminderTimeDTO>,
    val createdAt: LocalDateTime,
    val motivationalNote: String,
    val log: List<HabitLogEntryDTO>
) {
    fun isSuccessful(date: LocalDateTime): Boolean {
        return log.any { it.date == date && it.success }
    }

    fun getCurrentStreak(): Int = log.count { it.success }
}