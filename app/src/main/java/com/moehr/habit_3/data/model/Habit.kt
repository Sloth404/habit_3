package com.moehr.habit_3.data.model

import com.moehr.habit_3.data.model.dto.HabitLogEntryDTO
import com.moehr.habit_3.data.model.dto.ReminderTimeDTO
import java.io.Serializable
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Data class representing a Habit entity.
 *
 * @property id Unique identifier for the habit.
 * @property name The name/title of the habit.
 * @property type The type of habit (BUILD or BREAK).
 * @property target Target value to achieve (e.g., 10).
 * @property unit Unit of the target (e.g., "times", "minutes").
 * @property repeat How often the habit should be repeated (daily, weekly).
 * @property reminders List of reminder times for this habit.
 * @property createdAt Timestamp when the habit was created.
 * @property motivationalNote A custom motivational message for the habit.
 * @property log List of habit log entries representing past tracking data.
 */
data class  Habit(
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

    /**
     * Calculates the current streak of successful completions.
     *
     * @return Number of consecutive successful log entries.
     */
    fun getCurrentStreak(): Int = log.count { it.success }

    /**
     * Retrieves a list of dates where the habit was successfully completed.
     *
     * @return List of LocalDate objects representing successful completion dates.
     */
    fun getSuccessfulDates(): List<LocalDate> = log.filter { it.success }.map { it.date.toLocalDate() }
}
